package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.TrainersDto;
import java.util.List;

public interface TrainersService {
    TrainersDto createTrainer(TrainersDto trainersDto);
    TrainersDto getTrainerById(int id);
    TrainersDto updateTrainer(int id, TrainersDto trainersDto);
    void deleteTrainer(int id);
    List<TrainersDto> getAllTrainers();
}
