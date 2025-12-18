package me.risinu.jobportal.service;
import me.risinu.jobportal.dto.ApplicationsDto;
import java.util.List;

public interface ApplicationsService {
    ApplicationsDto createApplication(ApplicationsDto applicationsDto);
    ApplicationsDto getApplicationById(int id);
    ApplicationsDto updateApplication(int id, ApplicationsDto applicationsDto);
    void deleteApplication(int id);
    List<ApplicationsDto> getAllApplications();
    List<ApplicationsDto> getApplicationsByEmployerId(int employerId);
    List<ApplicationsDto> getApplicationsByJobSeekerId(int jobSeekerId);
}
