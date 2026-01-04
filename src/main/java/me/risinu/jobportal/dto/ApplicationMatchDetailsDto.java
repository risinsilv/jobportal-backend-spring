package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO that matches the strict JSON structure returned by the LLM match analysis.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMatchDetailsDto {

    private Double similarity_score;
    private String overall_match_level;

    private SkillsAnalysis skills_analysis;
    private ExperienceAnalysis experience_analysis;
    private EducationAnalysis education_analysis;

    private List<String> strengths;
    private List<String> concerns;

    private String overall_conclusion;
    private String recruiter_recommendation;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillsAnalysis {
        private List<String> matched_skills;
        private List<String> missing_skills;
        private List<String> optional_or_nice_to_have_missing;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceAnalysis {
        private List<String> relevant_experience;
        private List<String> experience_gaps;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationAnalysis {
        private List<String> matched_education;
        private List<String> education_gaps;
    }
}

