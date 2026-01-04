package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.ApplicationsDto;
import me.risinu.jobportal.dto.JobSeekersDto;

import java.util.List;

public interface ApplicationsService {
    ApplicationsDto createApplication(ApplicationsDto applicationsDto);
    ApplicationsDto getApplicationById(int id);
    ApplicationsDto updateApplication(int id, ApplicationsDto applicationsDto);
    void deleteApplication(int id);
    List<ApplicationsDto> getAllApplications();
    List<ApplicationsDto> getApplicationsByEmployerId(int employerId);
    List<ApplicationsDto> getApplicationsByJobSeekerId(int jobSeekerId);

    // --- secure methods ---
    List<ApplicationsDto> getApplicationsForJobSecure(int jobId, int employerId);
    JobSeekersDto getJobSeekerProfileSecure(int employerId, int jobId, int jobSeekerUserId);

    // Update only the application status; employer must own the underlying job
    ApplicationsDto updateApplicationStatusSecure(int applicationId, int employerId, String newStatus);
}
