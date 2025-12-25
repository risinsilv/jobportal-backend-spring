package me.risinu.jobportal.service.impl;


import jakarta.annotation.PostConstruct;
import me.risinu.jobportal.dto.ApplicationsDto;
import me.risinu.jobportal.entity.Applications;
import me.risinu.jobportal.entity.JobPostings;
import me.risinu.jobportal.entity.JobSeekers;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.ApplicationsRepo;
import me.risinu.jobportal.repo.JobPostingsRepo;
import me.risinu.jobportal.repo.JobSeekersRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.ApplicationsService;
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
    private UsersRepo usersRepository;

    @Autowired
    private JobSeekersRepo jobSeekersRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ApplicationsDto createApplication(ApplicationsDto dto) {
        Applications application = new Applications();
        JobPostings job = jobPostingsRepository.findById(dto.getJobId())
                .orElseThrow(() -> new RuntimeException("Job not found"));
        JobSeekers jobSeeker = jobSeekersRepository.findById(dto.getJobSeekerId())
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setStatus(Applications.Status.valueOf(dto.getStatus()));
        Applications saved = applicationsRepository.save(application);
        ApplicationsDto result = new ApplicationsDto();
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
}