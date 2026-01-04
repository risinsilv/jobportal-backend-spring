package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.ApplicationsDto;
import me.risinu.jobportal.dto.JobSeekersDto;
import me.risinu.jobportal.service.ApplicationsService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationsController {

    @Autowired
    private ApplicationsService applicationsService;

    @Autowired
    private JWT jwt;

    private boolean verifyTokenOwnedByUser(String token, int userId) {
        return jwt.verifyTokenOwnedByUser(token, userId);
    }

    @PostMapping
    public ResponseEntity<ApplicationsDto> create(@RequestBody ApplicationsDto dto) {
        return ResponseEntity.ok(applicationsService.createApplication(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationsDto> getById(@PathVariable int id) {
        return ResponseEntity.ok(applicationsService.getApplicationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationsDto> update(@PathVariable int id, @RequestBody ApplicationsDto dto) {
        return ResponseEntity.ok(applicationsService.updateApplication(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        applicationsService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ApplicationsDto>> getAll() {
        return ResponseEntity.ok(applicationsService.getAllApplications());
    }

    @GetMapping("/by-employer/{employerId}")
    public ResponseEntity<List<ApplicationsDto>> getByEmployerId(@PathVariable int employerId) {
        return ResponseEntity.ok(applicationsService.getApplicationsByEmployerId(employerId));
    }

    @GetMapping("/by-jobseeker/{jobSeekerId}")
    public ResponseEntity<List<ApplicationsDto>> getByJobSeekerId(@PathVariable int jobSeekerId) {
        return ResponseEntity.ok(applicationsService.getApplicationsByJobSeekerId(jobSeekerId));
    }

    // ---------------- secure endpoints ----------------

    /**
     * Secure: employer reads all applications for one of their jobs.
     */
    @GetMapping("/secure/by-job/{jobId}/employer/{employerId}")
    public ResponseEntity<List<ApplicationsDto>> getApplicationsByJobSecure(
            @RequestHeader("Authorization") String token,
            @PathVariable int jobId,
            @PathVariable int employerId
    ) {
        if (!verifyTokenOwnedByUser(token, employerId)) {
            return ResponseEntity.status(403).build();
        }

        try {
            return ResponseEntity.ok(applicationsService.getApplicationsForJobSecure(jobId, employerId));
        } catch (RuntimeException ex) {
            if ("Forbidden".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Secure: employer reads a job seeker's profile, only if that seeker applied to the employer's job.
     */
    @GetMapping("/secure/jobseeker-profile")
    public ResponseEntity<JobSeekersDto> getJobSeekerProfileSecure(
            @RequestHeader("Authorization") String token,
            @RequestParam int employerId,
            @RequestParam int jobId,
            @RequestParam int jobSeekerUserId
    ) {
        if (!verifyTokenOwnedByUser(token, employerId)) {
            return ResponseEntity.status(403).build();
        }

        try {
            return ResponseEntity.ok(applicationsService.getJobSeekerProfileSecure(employerId, jobId, jobSeekerUserId));
        } catch (RuntimeException ex) {
            if ("Forbidden".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.status(403).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Secure: employer update application status.
     */
    @PatchMapping("/secure/{applicationId}/status/employer/{employerId}")
    public ResponseEntity<ApplicationsDto> updateApplicationStatusSecure(
            @RequestHeader("Authorization") String token,
            @PathVariable int applicationId,
            @PathVariable int employerId,
            @RequestBody me.risinu.jobportal.dto.ApplicationStatusUpdateDto body
    ) {
        if (!verifyTokenOwnedByUser(token, employerId)) {
            return ResponseEntity.status(403).build();
        }

        try {
            return ResponseEntity.ok(
                    applicationsService.updateApplicationStatusSecure(applicationId, employerId, body != null ? body.getStatus() : null)
            );
        } catch (RuntimeException ex) {
            if ("Forbidden".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.status(403).build();
            }
            if ("Application not found".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Secure: job seeker creates an application for themselves.
     * Status is server-controlled (defaults to Applied).
     */
    @PostMapping("/secure")
    public ResponseEntity<ApplicationsDto> createSecure(
            @RequestHeader("Authorization") String token,
            @RequestBody ApplicationsDto dto
    ) {
        if (dto == null || dto.getJobSeekerId() == 0) {
            return ResponseEntity.badRequest().build();
        }
        if (!verifyTokenOwnedByUser(token, dto.getJobSeekerId())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(applicationsService.createApplication(dto));
    }
}
