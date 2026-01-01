package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.JobPostingsDto;
import me.risinu.jobportal.dto.JobPostingsSearchRequest;
import me.risinu.jobportal.dto.JobPostingSearchResultDto;
import me.risinu.jobportal.dto.JobPostingUpdateDto;
import me.risinu.jobportal.entity.Employer;
import me.risinu.jobportal.entity.JobPostings;
import me.risinu.jobportal.repo.EmployerRepo;
import me.risinu.jobportal.repo.JobPostingsRepo;
import me.risinu.jobportal.service.JobPostingsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPostingsServiceImpl implements JobPostingsService {

    @Autowired
    private JobPostingsRepo jobPostingsRepository;

    @Autowired
    private EmployerRepo employerRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static JobPostingsDto toDto(JobPostings jobPosting, ModelMapper modelMapper) {
        JobPostingsDto dto = modelMapper.map(jobPosting, JobPostingsDto.class);
        if (jobPosting.getEmployer() != null) {
            dto.setEmployerId(jobPosting.getEmployer().getId());
        }
        dto.setStatus(jobPosting.getStatus() != null ? jobPosting.getStatus().name() : null);
        dto.setExperienceLevel(jobPosting.getExperienceLevel() != null ? jobPosting.getExperienceLevel().name() : null);
        dto.setJobType(jobPosting.getJobType() != null ? jobPosting.getJobType().name() : null);
        dto.setWorkplaceType(jobPosting.getWorkplaceType() != null ? jobPosting.getWorkplaceType().name() : null);
        return dto;
    }

    private static void applyDtoToEntity(JobPostingsDto dto, JobPostings entity) {
        // Enums
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            entity.setStatus(JobPostings.Status.valueOf(dto.getStatus()));
        }
        if (dto.getExperienceLevel() != null && !dto.getExperienceLevel().isBlank()) {
            entity.setExperienceLevel(JobPostings.ExperienceLevel.valueOf(dto.getExperienceLevel()));
        }
        if (dto.getJobType() != null && !dto.getJobType().isBlank()) {
            entity.setJobType(JobPostings.JobType.valueOf(dto.getJobType()));
        }
        if (dto.getWorkplaceType() != null && !dto.getWorkplaceType().isBlank()) {
            entity.setWorkplaceType(JobPostings.WorkplaceType.valueOf(dto.getWorkplaceType()));
        }

        // Simple fields (ModelMapper handles most of these; keep explicit ones for clarity)
        entity.setSalaryMin(dto.getSalaryMin());
        entity.setSalaryMax(dto.getSalaryMax());
        entity.setCurrency(dto.getCurrency());
        entity.setResponsibilities(dto.getResponsibilities());
        entity.setNiceToHave(dto.getNiceToHave());
        entity.setOther(dto.getOther());
    }

    @Override
    public JobPostingsDto createJobPosting(JobPostingsDto dto) {
        JobPostings jobPosting = modelMapper.map(dto, JobPostings.class);

        Employer employer = employerRepository.findById(dto.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        jobPosting.setEmployer(employer);

        applyDtoToEntity(dto, jobPosting);

        // If status not provided, default Open
        if (jobPosting.getStatus() == null) {
            jobPosting.setStatus(JobPostings.Status.Open);
        }

        JobPostings saved = jobPostingsRepository.save(jobPosting);
        return toDto(saved, modelMapper);
    }

    @Override
    public JobPostingsDto getJobPostingById(int id) {
        JobPostings jobPosting = jobPostingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));
        return toDto(jobPosting, modelMapper);
    }

    @Override
    public JobPostingsDto updateJobPosting(int id, JobPostingsDto dto) {
        JobPostings existing = jobPostingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        modelMapper.map(dto, existing);

        if (dto.getEmployerId() != 0) {
            Employer employer = employerRepository.findById(dto.getEmployerId())
                    .orElseThrow(() -> new RuntimeException("Employer not found"));
            existing.setEmployer(employer);
        }

        applyDtoToEntity(dto, existing);

        JobPostings updated = jobPostingsRepository.save(existing);
        return toDto(updated, modelMapper);
    }

    @Override
    public void deleteJobPosting(int id) {
        if (!jobPostingsRepository.existsById(id)) {
            throw new RuntimeException("Job posting not found");
        }
        jobPostingsRepository.deleteById(id);
    }

    @Override
    public List<JobPostingsDto> getAllJobPostings() {
        return jobPostingsRepository.findAll().stream()
                .map(jobPosting -> toDto(jobPosting, modelMapper))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostingSearchResultDto> search(JobPostingsSearchRequest request) {
        Specification<JobPostings> spec = (root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            if (request == null) {
                // newest first
                query.orderBy(cb.desc(root.get("postedAt")));
                return cb.conjunction();
            }

            // newest first
            query.orderBy(cb.desc(root.get("postedAt")));

            // Title only (case-insensitive partial match)
            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                String like = "%" + request.getTitle().trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), like));
            }

            if (request.getLastHours() != null && request.getLastHours() > 0) {
                LocalDateTime cutoff = LocalDateTime.now().minusHours(request.getLastHours());
                predicates.add(cb.greaterThanOrEqualTo(root.get("postedAt"), cutoff));
            }

            // Simple equals filters
            if (request.getLocation() != null && !request.getLocation().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + request.getLocation().trim().toLowerCase() + "%"));
            }

            if (request.getCurrency() != null && !request.getCurrency().isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("currency")), request.getCurrency().trim().toUpperCase()));
            }

            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                predicates.add(cb.equal(root.get("status"), JobPostings.Status.valueOf(request.getStatus())));
            }

            if (request.getExperienceLevel() != null && !request.getExperienceLevel().isBlank()) {
                predicates.add(cb.equal(root.get("experienceLevel"), JobPostings.ExperienceLevel.valueOf(request.getExperienceLevel())));
            }

            if (request.getJobType() != null && !request.getJobType().isBlank()) {
                predicates.add(cb.equal(root.get("jobType"), JobPostings.JobType.valueOf(request.getJobType())));
            }

            if (request.getWorkplaceType() != null && !request.getWorkplaceType().isBlank()) {
                predicates.add(cb.equal(root.get("workplaceType"), JobPostings.WorkplaceType.valueOf(request.getWorkplaceType())));
            }

            if (request.getEmployerId() != null) {
                predicates.add(cb.equal(root.get("employer").get("id"), request.getEmployerId()));
            }

            // Salary range filters
            if (request.getSalaryMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salaryMin"), request.getSalaryMin()));
            }
            if (request.getSalaryMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salaryMax"), request.getSalaryMax()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = Pageable.unpaged();
        if (request != null && request.getPage() != null && request.getSize() != null) {
            pageable = PageRequest.of(Math.max(request.getPage(), 0), Math.max(request.getSize(), 1));
        }

        return jobPostingsRepository.findAll(spec, pageable).stream()
                .map(job -> {
                    String org = null;
                    if (job.getEmployer() != null) {
                        // Using Employer.companyName as organization
                        org = job.getEmployer().getCompanyName();
                    }
                    return new JobPostingSearchResultDto(
                            job.getJobId(),
                            job.getTitle(),
                            job.getLocation(),
                            job.getPostedAt(),
                            job.getExperienceLevel() != null ? job.getExperienceLevel().name() : null,
                            job.getJobType() != null ? job.getJobType().name() : null,
                            job.getWorkplaceType() != null ? job.getWorkplaceType().name() : null,
                            job.getSalaryMin(),
                            job.getSalaryMax(),
                            job.getCurrency(),
                            org
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostingSearchResultDto> getMyEmployerJobPostings(int employerId, Integer page, Integer size) {
        int p = page == null ? 0 : Math.max(page, 0);
        int s = size == null ? 20 : Math.max(size, 1);

        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "postedAt"));

        return jobPostingsRepository.findByEmployer_Id(employerId, pageable)
                .stream()
                .map(job -> {
                    String org = null;
                    if (job.getEmployer() != null) {
                        org = job.getEmployer().getCompanyName();
                    }
                    return new JobPostingSearchResultDto(
                            job.getJobId(),
                            job.getTitle(),
                            job.getLocation(),
                            job.getPostedAt(),
                            job.getExperienceLevel() != null ? job.getExperienceLevel().name() : null,
                            job.getJobType() != null ? job.getJobType().name() : null,
                            job.getWorkplaceType() != null ? job.getWorkplaceType().name() : null,
                            job.getSalaryMin(),
                            job.getSalaryMax(),
                            job.getCurrency(),
                            org
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    public JobPostingsDto updateJobPostingSelectedFields(int jobId, int employerId, JobPostingUpdateDto updateDto) {
        if (updateDto == null) {
            throw new IllegalArgumentException("Update payload is required");
        }

        JobPostings existing = jobPostingsRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        // Ownership check (prevents IDOR)
        if (existing.getEmployer() == null || existing.getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden: cannot update another employer's job posting");
        }

        // Allowed fields only (ignore nulls)
        if (updateDto.getSalaryMin() != null) {
            existing.setSalaryMin(updateDto.getSalaryMin());
        }
        if (updateDto.getSalaryMax() != null) {
            existing.setSalaryMax(updateDto.getSalaryMax());
        }
        if (updateDto.getCurrency() != null && !updateDto.getCurrency().isBlank()) {
            existing.setCurrency(updateDto.getCurrency().trim().toUpperCase());
        }

        if (updateDto.getExperienceLevel() != null && !updateDto.getExperienceLevel().isBlank()) {
            existing.setExperienceLevel(JobPostings.ExperienceLevel.valueOf(updateDto.getExperienceLevel().trim()));
        }
        if (updateDto.getJobType() != null && !updateDto.getJobType().isBlank()) {
            existing.setJobType(JobPostings.JobType.valueOf(updateDto.getJobType().trim()));
        }
        if (updateDto.getWorkplaceType() != null && !updateDto.getWorkplaceType().isBlank()) {
            existing.setWorkplaceType(JobPostings.WorkplaceType.valueOf(updateDto.getWorkplaceType().trim()));
        }
        if (updateDto.getStatus() != null && !updateDto.getStatus().isBlank()) {
            existing.setStatus(JobPostings.Status.valueOf(updateDto.getStatus().trim()));
        }

        JobPostings saved = jobPostingsRepository.save(existing);
        return toDto(saved, modelMapper);
    }

    @Override
    public void deleteJobPostingSecure(int jobId, int employerId) {
        JobPostings existing = jobPostingsRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        if (existing.getEmployer() == null || existing.getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden: cannot delete another employer's job posting");
        }

        jobPostingsRepository.delete(existing);
    }

}
