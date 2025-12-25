package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.ApplicationsDto;
import me.risinu.jobportal.service.ApplicationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationsController {

    @Autowired
    private ApplicationsService applicationsService;

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
}
