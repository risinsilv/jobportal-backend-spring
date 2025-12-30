package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.TrainersDto;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.TrainersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainersServiceImpl implements TrainersService {

    @Autowired
    private TrainersRepo trainersRepository;

    @Autowired
    private UsersRepo usersRepository;

    @Autowired
    private  TrainersRepo trainersRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TrainersDto createTrainer(TrainersDto trainersDto) {
        Trainers trainer = modelMapper.map(trainersDto, Trainers.class);
        Users user = usersRepository.findById(trainersDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        trainer.setUser(user);
        Trainers savedTrainer = trainersRepository.save(trainer);
        return modelMapper.map(savedTrainer, TrainersDto.class);
    }

    @Override
    public TrainersDto getTrainerById(int id) {
        Trainers trainer = trainersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        TrainersDto dto = modelMapper.map(trainer, TrainersDto.class);
        dto.setUserId(trainer.getUser().getId());
        return dto;
    }

    @Override
    public TrainersDto updateTrainer(int id, TrainersDto trainersDto) {
        Trainers existingTrainer = trainersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trainer not found"));

        // Manually set fields from trainersDto to existingTrainer
        existingTrainer.setCertifications(trainersDto.getCertifications());
        existingTrainer.setSpecialization(trainersDto.getSpecialization());
        existingTrainer.setCompany(trainersDto.getCompany());
        existingTrainer.setBio(trainersDto.getBio());
        // Add other fields as needed

        if (trainersDto.getUserId() != 0) {
            Users user = usersRepository.findById(trainersDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existingTrainer.setUser(user);
            existingTrainer.setId(id);
        }

        Trainers updatedTrainer = trainersRepository.save(existingTrainer);
        return modelMapper.map(updatedTrainer, TrainersDto.class);
    }

    @Override
    public void deleteTrainer(int id) {
        if (!trainersRepository.existsById(id)) {
            throw new RuntimeException("Trainer not found");
        }
        trainersRepository.deleteById(id);
    }

    @Override
    public List<TrainersDto> getAllTrainers() {
        return trainersRepository.findAll().stream()
                .map(trainer -> {
                    TrainersDto dto = modelMapper.map(trainer, TrainersDto.class);
                    dto.setUserId(trainer.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
