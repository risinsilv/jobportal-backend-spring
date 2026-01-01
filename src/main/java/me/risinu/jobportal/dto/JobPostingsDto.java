package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingsDto {
    private int id;
    private int jobId;
    private int employerId;

    private String title;
    private String description;
    private String location;

    /** Legacy field (kept for compatibility). */
    private String salary;

    // New structured salary fields
    private Integer salaryMin;
    private Integer salaryMax;
    private String currency;

    private String experienceLevel;
    private String jobType;
    private String workplaceType;

    private String responsibilities;
    private String requirements;
    private String niceToHave;
    private String other;

    private String status;
}