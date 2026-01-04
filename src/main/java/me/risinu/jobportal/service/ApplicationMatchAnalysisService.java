package me.risinu.jobportal.service;

/**
 * Runs CV-vs-Job matching using OpenRouter and persists the results against the Application.
 * Intended for internal/server-side use (no direct FE access required).
 */
public interface ApplicationMatchAnalysisService {

    /**
     * Analyze a specific application by fetching:
     * - CV PDF for the application's job seeker
     * - Job description from the job posting
     * Then sends them to OpenRouter and stores the returned JSON under ApplicationMatchDetails.
     */
    void analyzeAndStoreForApplication(int applicationId);
}

