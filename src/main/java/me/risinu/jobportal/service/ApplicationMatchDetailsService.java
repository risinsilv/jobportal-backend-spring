package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.ApplicationMatchDetailsResponseDto;

import java.util.List;

public interface ApplicationMatchDetailsService {

    /**
     * Secure: employer reads match details for all applications of a job they own.
     */
    List<ApplicationMatchDetailsResponseDto> getMatchDetailsByJobSecure(int employerId, int jobId);

    /**
     * Secure: employer reads match details for a single application (must belong to employer).
     */
    ApplicationMatchDetailsResponseDto getMatchDetailsByApplicationSecure(int employerId, int applicationId);
}

