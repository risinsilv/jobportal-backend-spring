package me.risinu.jobportal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.risinu.jobportal.dto.ApplicationMatchDetailsDto;
import me.risinu.jobportal.dto.ApplicationMatchDetailsResponseDto;
import me.risinu.jobportal.entity.ApplicationMatchDetails;
import me.risinu.jobportal.entity.Applications;
import me.risinu.jobportal.entity.JobPostings;
import me.risinu.jobportal.repo.ApplicationMatchDetailsRepo;
import me.risinu.jobportal.repo.ApplicationsRepo;
import me.risinu.jobportal.repo.JobPostingsRepo;
import me.risinu.jobportal.service.ApplicationMatchDetailsService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ApplicationMatchDetailsServiceImpl implements ApplicationMatchDetailsService {

    private final JobPostingsRepo jobPostingsRepo;
    private final ApplicationsRepo applicationsRepo;
    private final ApplicationMatchDetailsRepo matchDetailsRepo;
    private final ObjectMapper objectMapper;

    public ApplicationMatchDetailsServiceImpl(
            JobPostingsRepo jobPostingsRepo,
            ApplicationsRepo applicationsRepo,
            ApplicationMatchDetailsRepo matchDetailsRepo,
            ObjectMapper objectMapper
    ) {
        this.jobPostingsRepo = jobPostingsRepo;
        this.applicationsRepo = applicationsRepo;
        this.matchDetailsRepo = matchDetailsRepo;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ApplicationMatchDetailsResponseDto> getMatchDetailsByJobSecure(int employerId, int jobId) {
        JobPostings job = jobPostingsRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getEmployer() == null || job.getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden");
        }

        // Load all applications for this job and map match details (if available)
        return applicationsRepo.findByJob_JobId(jobId)
                .stream()
                .map(app -> {
                    ApplicationMatchDetails md = app.getMatchDetails();
                    if (md == null) {
                        return null;
                    }
                    return toResponseDto(app, md);
                })
                .filter(r -> r != null)
                .sorted(Comparator.comparing(ApplicationMatchDetailsResponseDto::getSimilarityScore, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    public ApplicationMatchDetailsResponseDto getMatchDetailsByApplicationSecure(int employerId, int applicationId) {
        Applications app = applicationsRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getJob() == null || app.getJob().getEmployer() == null || app.getJob().getEmployer().getId() != employerId) {
            throw new RuntimeException("Forbidden");
        }

        ApplicationMatchDetails md = app.getMatchDetails();
        if (md == null) {
            throw new RuntimeException("Match details not found");
        }

        return toResponseDto(app, md);
    }

    private ApplicationMatchDetailsResponseDto toResponseDto(Applications app, ApplicationMatchDetails md) {
        ApplicationMatchDetailsDto parsed = null;
        String raw = md.getAnalysisJson();
        if (raw != null && !raw.isBlank()) {
            try {
                parsed = objectMapper.readValue(raw, ApplicationMatchDetailsDto.class);
            } catch (Exception ignored) {
                // Keep parsed null; still return raw.
            }
        }

        return new ApplicationMatchDetailsResponseDto(
                md.getId(),
                app.getApplicationId(),
                app.getJob() != null ? app.getJob().getJobId() : 0,
                md.getSimilarityScore(),
                md.getOverallMatchLevel(),
                parsed,
                md.getAnalysisJson(),
                md.getCreatedAt(),
                md.getUpdatedAt()
        );
    }
}

