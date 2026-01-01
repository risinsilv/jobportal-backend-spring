package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search/filter request for job postings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingsSearchRequest {
    /** Job title filter (case-insensitive, partial match). */
    private String title;

    private String location;

    private Integer salaryMin;
    private Integer salaryMax;
    private String currency;

    private String experienceLevel; // matches JobPostings.ExperienceLevel
    private String jobType;         // matches JobPostings.JobType
    private String workplaceType;   // matches JobPostings.WorkplaceType

    private String status;          // matches JobPostings.Status

    private Integer employerId;

    /** Return jobs posted within the last N hours (optional). */
    private Integer lastHours;

    /** Pagination (optional). */
    private Integer page;
    private Integer size;
}
