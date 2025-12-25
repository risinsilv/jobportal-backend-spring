package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.TrainersDto;
import me.risinu.jobportal.service.TrainersService;
import me.risinu.jobportal.util.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
public class TrainersController {


    @Autowired
    private TrainersService trainersService;

    @Autowired
    private JWT tokenGenerator;

    private boolean verifyToken(String token) {
        return tokenGenerator.verifyToken(token);
    }

    @PostMapping
    public ResponseEntity<TrainersDto> createTrainer(@RequestHeader("Authorization") String token, @RequestBody TrainersDto trainersDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        TrainersDto createdTrainer = trainersService.createTrainer(trainersDto);
        return ResponseEntity.ok(createdTrainer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainersDto> getTrainerById(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        TrainersDto trainer = trainersService.getTrainerById(id);
        return ResponseEntity.ok(trainer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainersDto> updateTrainer(@RequestHeader("Authorization") String token, @PathVariable int id, @RequestBody TrainersDto trainersDto) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        TrainersDto updatedTrainer = trainersService.updateTrainer(id, trainersDto);
        return ResponseEntity.ok(updatedTrainer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@RequestHeader("Authorization") String token, @PathVariable int id) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        trainersService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TrainersDto>> getAllTrainers(@RequestHeader("Authorization") String token) {
        if (!verifyToken(token)) {
            return ResponseEntity.status(401).build();
        }
        List<TrainersDto> trainers = trainersService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }
}
