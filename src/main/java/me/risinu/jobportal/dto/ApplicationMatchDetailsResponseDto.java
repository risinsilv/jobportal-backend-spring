package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Wrapper response for returning match details linked to an application/job.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMatchDetailsResponseDto {

    private Long id;
    private int applicationId;
    private int jobId;

    private Double similarityScore;
    private String overallMatchLevel;

    /** Parsed JSON into a typed DTO (same structure as LLM output). */
    private ApplicationMatchDetailsDto analysis;

    /** Raw JSON as stored in DB. */
    private String analysisJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

