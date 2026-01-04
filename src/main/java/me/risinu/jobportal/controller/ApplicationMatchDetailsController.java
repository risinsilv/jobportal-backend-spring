package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.ApplicationMatchDetailsResponseDto;
import me.risinu.jobportal.service.ApplicationMatchDetailsService;
import me.risinu.jobportal.util.JWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/application-match")
public class ApplicationMatchDetailsController {

    private final ApplicationMatchDetailsService matchDetailsService;
    private final JWT jwt;

    public ApplicationMatchDetailsController(ApplicationMatchDetailsService matchDetailsService, JWT jwt) {
        this.matchDetailsService = matchDetailsService;
        this.jwt = jwt;
    }

    private boolean verifyTokenOwnedByUser(String token, int userId) {
        return jwt.verifyTokenOwnedByUser(token, userId);
    }

    /**
     * Secure: get match details for all applications of a job (employer must own the job).
     */
    @GetMapping("/secure/by-job/{jobId}/employer/{employerId}")
    public ResponseEntity<List<ApplicationMatchDetailsResponseDto>> getByJobSecure(
            @RequestHeader("Authorization") String token,
            @PathVariable int jobId,
            @PathVariable int employerId
    ) {
        if (!verifyTokenOwnedByUser(token, employerId)) {
            return ResponseEntity.status(403).build();
        }

        try {
            return ResponseEntity.ok(matchDetailsService.getMatchDetailsByJobSecure(employerId, jobId));
        } catch (RuntimeException ex) {
            if ("Forbidden".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.status(403).build();
            }
            if ("Job not found".equalsIgnoreCase(ex.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

//    /**
//     * Secure: get match details for a single application (must belong to employer).
//     */
//    @GetMapping("/secure/by-application/{applicationId}/employer/{employerId}")
//    public ResponseEntity<ApplicationMatchDetailsResponseDto> getByApplicationSecure(
//            @RequestHeader("Authorization") String token,
//            @PathVariable int applicationId,
//            @PathVariable int employerId
//    ) {
//        if (!verifyTokenOwnedByUser(token, employerId)) {
//            return ResponseEntity.status(403).build();
//        }
//
//        try {
//            return ResponseEntity.ok(matchDetailsService.getMatchDetailsByApplicationSecure(employerId, applicationId));
//        } catch (RuntimeException ex) {
//            if ("Forbidden".equalsIgnoreCase(ex.getMessage())) {
//                return ResponseEntity.status(403).build();
//            }
//            if ("Application not found".equalsIgnoreCase(ex.getMessage())) {
//                return ResponseEntity.notFound().build();
//            }
//            if ("Match details not found".equalsIgnoreCase(ex.getMessage())) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.badRequest().build();
//        }
//    }
}

