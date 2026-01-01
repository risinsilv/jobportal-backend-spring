package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.JobPostingsDto;
import me.risinu.jobportal.dto.JobPostingsSearchRequest;
import me.risinu.jobportal.dto.JobPostingSearchResultDto;
import me.risinu.jobportal.dto.JobPostingUpdateDto;

import java.util.List;

public interface JobPostingsService {
    JobPostingsDto createJobPosting(JobPostingsDto jobPostingsDto);
    JobPostingsDto getJobPostingById(int id);
    JobPostingsDto updateJobPosting(int id, JobPostingsDto jobPostingsDto);
    void deleteJobPosting(int id);
    List<JobPostingsDto> getAllJobPostings();

    List<JobPostingSearchResultDto> search(JobPostingsSearchRequest request);

    List<JobPostingSearchResultDto> getMyEmployerJobPostings(int employerId, Integer page, Integer size);

    JobPostingsDto updateJobPostingSelectedFields(int jobId, int employerId, JobPostingUpdateDto updateDto);

    void deleteJobPostingSecure(int jobId, int employerId);
}
