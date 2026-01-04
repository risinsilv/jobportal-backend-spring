package me.risinu.jobportal.service.impl;


import me.risinu.jobportal.dto.ApplicationsDto;
import me.risinu.jobportal.dto.JobSeekersDto;
import me.risinu.jobportal.entity.Applications;
import me.risinu.jobportal.entity.JobPostings;
import me.risinu.jobportal.entity.JobSeekers;
import me.risinu.jobportal.repo.ApplicationsRepo;
import me.risinu.jobportal.repo.JobPostingsRepo;
import me.risinu.jobportal.repo.JobSeekersRepo;
import me.risinu.jobportal.service.ApplicationsService;
import me.risinu.jobportal.service.ApplicationMatchAnalysisService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationsServiceImpl implements ApplicationsService {

    @Autowired
    private ApplicationsRepo applicationsRepository;

    @Autowired
    private JobPostingsRepo jobPostingsRepository;

    @Autowired
    private JobSeekersRepo jobSeekersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ApplicationMatchAnalysisService applicationMatchAnalysisService;


    @Override
    public ApplicationsDto createApplication(ApplicationsDto dto) {
        Applications application = new Applications();
        JobPostings job = jobPostingsRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        JobSeekers jobSeeker = jobSeekersRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
        application.setJob(job);
        application.setJobSeeker(jobSeeker);

        // Server-controlled default status (do not accept status from client on creation)
        application.setStatus(Applications.Status.Applied);

        Applications saved = applicationsRepository.save(application);

        // Fire-and-forget analysis (runs in background executor)
        applicationMatchAnalysisService.analyzeAndStoreForApplication(saved.getApplicationId());

        ApplicationsDto result = new ApplicationsDto();
        result.setApplicationId(saved.getApplicationId());
        result.setJobId(saved.getJob().getJobId());
        result.setJobSeekerId(saved.getJobSeeker().getId());
        result.setStatus(saved.getStatus().name());
        return result;
    }

    @Override
    public ApplicationsDto getApplicationById(int id) {
        Applications application = applicationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        ApplicationsDto dto = modelMapper.map(application, ApplicationsDto.class);
        dto.setJobId(application.getJob().getJobId());
        dto.setJobSeekerId(application.getJobSeeker().getId());
        dto.setStatus(application.getStatus().name());
        return dto;
    }

    @Override
    public ApplicationsDto updateApplication(int id, ApplicationsDto dto) {
        Applications existing = applicationsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        if (dto.getJobId() != 0) {
            JobPostings job = jobPostingsRepository.findById(dto.getJobId())
                    .orElseThrow(() -> new RuntimeException("Job not found"));
            existing.setJob(job);
        }
        if (dto.getJobSeekerId() != 0) {
            JobSeekers jobSeeker = jobSeekersRepository.findById(dto.getJobSeekerId())
                    .orElseThrow(() -> new RuntimeException("Job seeker not found"));
            existing.setJobSeeker(jobSeeker);
        }
        if (dto.getStatus() != null) {
            existing.setStatus(Applications.Status.valueOf(dto.getStatus()));
        }
        existing.setStatus(dto.getStatus() != null ? Applications.Status.valueOf(dto.getStatus()) : existing.getStatus());
        Applications updated = applicationsRepository.save(existing);
        ApplicationsDto result = new ApplicationsDto();
        result.setJobId(updated.getJob().getJobId());
        result.setJobSeekerId(updated.getJobSeeker().getId());
        result.setStatus(updated.getStatus().name());
        return result;
    }

    @Override
    public void deleteApplication(int id) {
        if (!applicationsRepository.existsById(id)) {
            throw new RuntimeException("Application not found");
        }
        applicationsRepository.deleteById(id);
    }

    @Override
    public List<ApplicationsDto> getAllApplications() {
        return applicationsRepository.findAll().stream()
                .map(application -> {
                    ApplicationsDto dto = modelMapper.map(application, ApplicationsDto.class);
                    dto.setJobId(application.getJob().getJobId());
                    dto.setJobSeekerId(application.getJobSeeker().getId());
                    dto.setStatus(application.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationsDto> getApplicationsByEmployerId(int employerId) {
        return applicationsRepository.findByJob_Employer_Id(employerId)
                .stream()
                .map(application -> {
                    ApplicationsDto dto = new ApplicationsDto();
                    dto.setApplicationId(application.getApplicationId());
                    dto.setJobId(application.getJob().getJobId());
                    dto.setJobSeekerId(application.getJobSeeker().getId());
                    dto.setStatus(application.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationsDto> getApplicationsByJobSeekerId(int jobSeekerId) {
        return applicationsRepository.findByJobSeeker_Id(jobSeekerId)
                .stream()
                .map(application -> {
                    ApplicationsDto dto = new ApplicationsDto();
                    dto.setApplicationId(application.getApplicationId());
                    dto.setJobId(application.getJob().getJobId());
                    dto.setJobSeekerId(application.getJobSeeker().getId());
                    dto.setStatus(application.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ----------------- secure methods -----------------

    /**
     * Employer-scoped listing of applications for a single job.
     * Prevents an employer from reading applications for jobs they don't own.
     */
    @Override
    public List<ApplicationsDto> getApplicationsForJobSecure(int jobId, int employerId) {
        JobPostings job = jobPostingsRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getEmployer() == null || job.getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden");
        }

        return applicationsRepository.findByJob_JobId(jobId)
                .stream()
                .map(application -> {
                    ApplicationsDto dto = new ApplicationsDto();
                    dto.setApplicationId(application.getApplicationId());
                    dto.setJobId(application.getJob().getJobId());
                    dto.setJobSeekerId(application.getJobSeeker().getId());
                    dto.setStatus(application.getStatus().name());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Employer can access a job seeker's profile only if the seeker applied to this employer's job.
     */
    @Override
    public JobSeekersDto getJobSeekerProfileSecure(int employerId, int jobId, int jobSeekerUserId) {
        JobPostings job = jobPostingsRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getEmployer() == null || job.getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden");
        }

        JobSeekers seeker = jobSeekersRepository.findById(jobSeekerUserId)
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));

        boolean hasAppliedToThisJob = applicationsRepository.findByJob_JobId(jobId)
                .stream()
                .anyMatch(a -> a.getJobSeeker() != null && a.getJobSeeker().getId() == jobSeekerUserId);

        if (!hasAppliedToThisJob) {
            throw new RuntimeException("Forbidden");
        }

        JobSeekersDto dto = modelMapper.map(seeker, JobSeekersDto.class);
        dto.setUserId(seeker.getUser() != null ? seeker.getUser().getId() : 0);
        return dto;
    }

    @Override
    public ApplicationsDto updateApplicationStatusSecure(int applicationId, int employerId, String newStatus) {
        if (newStatus == null || newStatus.isBlank()) {
            throw new RuntimeException("Status is required");
        }

        Applications application = applicationsRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getJob() == null || application.getJob().getEmployer() == null
                || application.getJob().getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden");
        }

        Applications.Status status;
        try {
            status = Applications.Status.valueOf(newStatus);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid status");
        }

        application.setStatus(status);
        Applications saved = applicationsRepository.save(application);

        ApplicationsDto dto = new ApplicationsDto();
        dto.setApplicationId(saved.getApplicationId());
        dto.setJobId(saved.getJob().getJobId());
        dto.setJobSeekerId(saved.getJobSeeker().getId());
        dto.setStatus(saved.getStatus().name());
        return dto;
    }
}
