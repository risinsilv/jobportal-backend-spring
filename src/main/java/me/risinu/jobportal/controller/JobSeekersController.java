package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.JobSeekersDto;
import me.risinu.jobportal.service.JobSeekersService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/jobseekers")
public class JobSeekersController {

    @Autowired
    private JobSeekersService jobSeekersService;

    @Autowired
    private JWT tokenGenerator;

    private boolean verifyToken(String token) {
        return tokenGenerator.verifyToken(token);
    }

    @PostMapping
    public ResponseEntity<JobSeekersDto> createJobSeeker(@RequestHeader("Authorization") String token, @RequestBody JobSeekersDto jobSeekersDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        JobSeekersDto createdJobSeeker = jobSeekersService.createJobSeeker(jobSeekersDto);
        return ResponseEntity.ok(createdJobSeeker);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSeekersDto> getJobSeekerById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        JobSeekersDto jobSeeker = jobSeekersService.getJobSeekerById(id);
        return ResponseEntity.ok(jobSeeker);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobSeekersDto> updateJobSeeker(@RequestHeader("Authorization") String token, @PathVariable int id, @RequestBody JobSeekersDto jobSeekersDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        JobSeekersDto updatedJobSeeker = jobSeekersService.updateJobSeeker(id, jobSeekersDto);
        return ResponseEntity.ok(updatedJobSeeker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobSeeker(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        jobSeekersService.deleteJobSeeker(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<JobSeekersDto>> getAllJobSeekers(@RequestHeader("Authorization") String token) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        List<JobSeekersDto> jobSeekers = jobSeekersService.getAllJobSeekers();
        return ResponseEntity.ok(jobSeekers);
    }

    @PostMapping("/upload-cv/{id}")
    public ResponseEntity<Void> uploadCv(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestParam("cv") MultipartFile cvFile) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        jobSeekersService.uploadCv(id, cvFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cv/{userId}")
    public ResponseEntity<byte[]> getCv(@RequestHeader("Authorization") String token, @PathVariable int userId) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        byte[] pdf = jobSeekersService.getCv(userId);
        if (pdf == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=cv_" + userId + ".pdf")
                .body(pdf);
    }

    @PostMapping("/update-cv/{id}")
    public ResponseEntity<Void> updateCv(
            @RequestHeader("Authorization") String token,
            @PathVariable int id,
            @RequestParam("cv") MultipartFile cvFile) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        jobSeekersService.uploadCv(id, cvFile);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/cv/{userId}")
    public ResponseEntity<Void> deleteCv(@RequestHeader("Authorization") String token, @PathVariable int userId) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        jobSeekersService.deleteCv(userId);
        return ResponseEntity.noContent().build();
    }

}
