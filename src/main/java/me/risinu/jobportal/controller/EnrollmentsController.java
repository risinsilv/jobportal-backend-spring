package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.EnrollmentsDto;
import me.risinu.jobportal.service.EnrollmentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentsController {
    @Autowired
    private EnrollmentsService enrollmentsService;

    @PostMapping
    public ResponseEntity<EnrollmentsDto> createEnrollment(@RequestBody EnrollmentsDto enrollmentsDto) {
        EnrollmentsDto created = enrollmentsService.createEnrollment(enrollmentsDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentsDto> getEnrollmentById(@PathVariable int id) {
        EnrollmentsDto enrollment = enrollmentsService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentsDto> updateEnrollment(@PathVariable int id, @RequestBody EnrollmentsDto enrollmentsDto) {
        EnrollmentsDto updated = enrollmentsService.updateEnrollment(id, enrollmentsDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable int id) {
        enrollmentsService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentsDto>> getAllEnrollments() {
        List<EnrollmentsDto> enrollments = enrollmentsService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentsDto>> getEnrollmentsByUserId(@PathVariable int userId) {
        List<EnrollmentsDto> enrollments = enrollmentsService.getEnrollmentsByUserId(userId);
        return ResponseEntity.ok(enrollments);
    }

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<EnrollmentsDto>> getEnrollmentsByCourseId(@PathVariable int courseId) {
        List<EnrollmentsDto> enrollments = enrollmentsService.getEnrollmentsByCourseId(courseId);
        return ResponseEntity.ok(enrollments);
    }
}
