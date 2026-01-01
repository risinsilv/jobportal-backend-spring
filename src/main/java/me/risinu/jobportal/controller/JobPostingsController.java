package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.JobPostingsDto;
import me.risinu.jobportal.dto.JobPostingsSearchRequest;
import me.risinu.jobportal.dto.JobPostingSearchResultDto;
import me.risinu.jobportal.dto.JobPostingUpdateDto;
import me.risinu.jobportal.service.JobPostingsService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/jobpostings")
public class JobPostingsController {

    private static final Logger log = LoggerFactory.getLogger(JobPostingsController.class);

    @Autowired
    private JobPostingsService jobPostingsService;

    @Autowired
    private JWT tokenGenerator;

    private boolean verifyToken(String token) {
        return tokenGenerator.verifyToken(token);
    }

    @PostMapping
    public ResponseEntity<JobPostingsDto> createJobPosting(@RequestHeader("Authorization") String token, @RequestBody JobPostingsDto jobPostingsDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        JobPostingsDto created = jobPostingsService.createJobPosting(jobPostingsDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostingsDto> getJobPostingById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        JobPostingsDto jobPosting = jobPostingsService.getJobPostingById(id);
        return ResponseEntity.ok(jobPosting);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobPostingsDto> updateJobPosting(@RequestHeader("Authorization") String token, @PathVariable int id, @RequestBody JobPostingsDto jobPostingsDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        JobPostingsDto updated = jobPostingsService.updateJobPosting(id, jobPostingsDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobPosting(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Integer employerId = tokenGenerator.extractUserId(token).orElse(null);
        if (employerId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            jobPostingsService.deleteJobPostingSecure(id, employerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("forbidden")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<JobPostingsDto>> getAllJobPostings(@RequestHeader("Authorization") String token) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        List<JobPostingsDto> postings = jobPostingsService.getAllJobPostings();
        return ResponseEntity.ok(postings);
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobPostingSearchResultDto>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer salaryMin,
            @RequestParam(required = false) Integer salaryMax,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String workplaceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer employerId,
            @RequestParam(required = false) Integer lastHours,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {

        log.info(
                "Job search request params: title={}, location={}, salaryMin={}, salaryMax={}, currency={}, experienceLevel={}, jobType={}, workplaceType={}, status={}, employerId={}, lastHours={}, page={}, size={}",
                title, location, salaryMin, salaryMax, currency, experienceLevel, jobType, workplaceType, status, employerId, lastHours, page, size
        );

        JobPostingsSearchRequest request = new JobPostingsSearchRequest(
                title,
                location,
                salaryMin,
                salaryMax,
                currency,
                experienceLevel,
                jobType,
                workplaceType,
                status,
                employerId,
                lastHours,
                page,
                size
        );

        log.info("Job search request DTO: {}", request);

        List<JobPostingSearchResultDto> results = jobPostingsService.search(request);
        log.info("Job search results (count={}): {}", results == null ? 0 : results.size(), results);

        return ResponseEntity.ok(results);

    }

    @GetMapping("/my")
    public ResponseEntity<List<JobPostingSearchResultDto>> getMyEmployerJobs(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Integer userId = tokenGenerator.extractUserId(token).orElse(null);
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        // Employer id == user id (MapsId)
        List<JobPostingSearchResultDto> results = jobPostingsService.getMyEmployerJobPostings(userId, page, size);
        return ResponseEntity.ok(results);
    }

    @PatchMapping("/{jobId}")
    public ResponseEntity<JobPostingsDto> updateJobPostingSelectedFields(
            @RequestHeader("Authorization") String token,
            @PathVariable int jobId,
            @RequestBody JobPostingUpdateDto updateDto
    ) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }

        Integer employerId = tokenGenerator.extractUserId(token).orElse(null);
        if (employerId == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            JobPostingsDto updated = jobPostingsService.updateJobPostingSelectedFields(jobId, employerId, updateDto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("forbidden")) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
}
