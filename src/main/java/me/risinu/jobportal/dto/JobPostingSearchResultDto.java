package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

import java.time.LocalDateTime;

/**
 * Minimal payload for job search results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingSearchResultDto {
    private int jobId;
    private String title;
    private String location;
    private LocalDateTime postedAt;
    private String experienceLevel;
    private String JobType;
    private String worlkplaceType;
    private Integer salaryMin;
    private Integer salaryMax;
    private String currency;
    /** Employer organization/company name. */
    private String organization;
}

