package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.JobPostingsDto;
import java.util.List;

public interface JobPostingsService {
    JobPostingsDto createJobPosting(JobPostingsDto jobPostingsDto);
    JobPostingsDto getJobPostingById(int id);
    JobPostingsDto updateJobPosting(int id, JobPostingsDto jobPostingsDto);
    void deleteJobPosting(int id);
    List<JobPostingsDto> getAllJobPostings();
}
