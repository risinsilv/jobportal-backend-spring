package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.EmployerDto;
import me.risinu.jobportal.service.EmployerService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    @Autowired
    private EmployerService employerService;

    @Autowired
    private JWT tokenGenerator;

    private boolean verifyToken(String token) {
        return tokenGenerator.verifyToken(token);
    }

    @PostMapping("/create")
    public ResponseEntity<EmployerDto> createEmployer(@RequestBody EmployerDto employerDto) {
        EmployerDto createdEmployer = employerService.createEmployer(employerDto);
        return ResponseEntity.ok(createdEmployer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployerDto> getEmployerById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        EmployerDto employer = employerService.getEmployerById(id);
        return ResponseEntity.ok(employer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployerDto> updateEmployer(@RequestHeader("Authorization") String token, @PathVariable int id, @RequestBody EmployerDto employerDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        EmployerDto updatedEmployer = employerService.updateEmployer(id, employerDto);
        return ResponseEntity.ok(updatedEmployer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployer(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        employerService.deleteEmployer(id);
        return ResponseEntity.noContent().build();
    }
}
