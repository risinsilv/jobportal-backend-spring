package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.CoursesDto;
import me.risinu.jobportal.service.CoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CoursesController {

    @Autowired
    private CoursesService coursesService;

    @PostMapping
    public ResponseEntity<CoursesDto> create(@RequestBody CoursesDto dto) {
        return ResponseEntity.ok(coursesService.createCourse(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoursesDto> getById(@PathVariable int id) {
        return ResponseEntity.ok(coursesService.getCourseById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoursesDto> update(@PathVariable int id, @RequestBody CoursesDto dto) {
        return ResponseEntity.ok(coursesService.updateCourse(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        coursesService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CoursesDto>> getAll() {
        return ResponseEntity.ok(coursesService.getAllCourses());
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<CoursesDto>> getCoursesByTrainerId(@PathVariable int trainerId) {
        return ResponseEntity.ok(coursesService.getCoursesByTrainerId(trainerId));
    }
}
