package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.JobPostingsDto;
import me.risinu.jobportal.entity.Employer;
import me.risinu.jobportal.entity.JobPostings;
import me.risinu.jobportal.entity.JobSeekers;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.EmployerRepo;
import me.risinu.jobportal.repo.JobPostingsRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.JobPostingsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPostingsServiceImpl implements JobPostingsService {

    @Autowired
    private JobPostingsRepo jobPostingsRepository;

    @Autowired
    private UsersRepo usersRepository;

    @Autowired
    private EmployerRepo employerRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public JobPostingsDto createJobPosting(JobPostingsDto dto) {
        JobPostings jobPosting = modelMapper.map(dto, JobPostings.class);
        Employer employer = employerRepository.findById(dto.getEmployerId())
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        jobPosting.setEmployer(employer);
        jobPosting.setStatus(JobPostings.Status.valueOf(dto.getStatus()));
        JobPostings saved = jobPostingsRepository.save(jobPosting);
        JobPostingsDto result = modelMapper.map(saved, JobPostingsDto.class);
        result.setEmployerId(saved.getEmployer().getId());
        result.setStatus(saved.getStatus().name());
        return result;
    }

    @Override
    public JobPostingsDto getJobPostingById(int id) {
        JobPostings jobPosting = jobPostingsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));
        JobPostingsDto dto = modelMapper.map(jobPosting, JobPostingsDto.class);
        dto.setEmployerId(jobPosting.getEmployer().getId());
        dto.setStatus(jobPosting.getStatus().name());
        return dto;
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
        if (dto.getStatus() != null) {
            existing.setStatus(JobPostings.Status.valueOf(dto.getStatus()));
        }
        JobPostings updated = jobPostingsRepository.save(existing);
        JobPostingsDto result = modelMapper.map(updated, JobPostingsDto.class);
        result.setEmployerId(updated.getEmployer().getId());
        result.setStatus(updated.getStatus().name());
        return result;
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
                .map(jobPosting -> {
                    JobPostingsDto dto = modelMapper.map(jobPosting, JobPostingsDto.class);
                    dto.setEmployerId(jobPosting.getEmployer().getId());
                    dto.setStatus(jobPosting.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
