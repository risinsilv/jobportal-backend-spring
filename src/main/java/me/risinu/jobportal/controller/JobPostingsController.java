package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.JobPostingsDto;
import me.risinu.jobportal.service.JobPostingsService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobpostings")
public class JobPostingsController {

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
        jobPostingsService.deleteJobPosting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<JobPostingsDto>> getAllJobPostings(@RequestHeader("Authorization") String token) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        List<JobPostingsDto> postings = jobPostingsService.getAllJobPostings();
        return ResponseEntity.ok(postings);
    }
}
