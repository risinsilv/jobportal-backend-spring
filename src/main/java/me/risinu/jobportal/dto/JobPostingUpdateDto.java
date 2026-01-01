package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Partial update DTO for JobPostings.
 * Only fields in this DTO are allowed to be updated by the employer.
 * Any null field will be ignored (won't overwrite existing values).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingUpdateDto {
    private Integer salaryMin;
    private Integer salaryMax;

    /** ISO 4217 currency code (e.g., LKR, USD). */
    private String currency;

    /** Enum names: INTERNSHIP, ENTRY_LEVEL, ASSOCIATE, MID_SENIOR_LEVEL, DIRECTOR, EXECUTIVE */
    private String experienceLevel;

    /** Enum names: FULL_TIME, PART_TIME, CONTRACT, TEMPORARY, INTERN, FREELANCE */
    private String jobType;

    /** Enum names should match JobPostings.WorkplaceType */
    private String workplaceType;

    /** Enum names should match JobPostings.Status (e.g., Open, Closed). */
    private String status;
}

