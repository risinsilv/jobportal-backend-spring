package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.CoursesDto;
import me.risinu.jobportal.entity.Courses;
import me.risinu.jobportal.entity.Trainers;
import me.risinu.jobportal.entity.Users;

import me.risinu.jobportal.repo.CoursesRepo;
import me.risinu.jobportal.repo.TrainersRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.CoursesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoursesServiceImpl implements CoursesService {

    @Autowired
    private CoursesRepo coursesRepository;

    @Autowired
    private UsersRepo usersRepository;

    @Autowired
    private TrainersRepo trainersRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CoursesDto createCourse(CoursesDto coursesDto) {
        Courses course = modelMapper.map(coursesDto, Courses.class);
        Trainers trainer = trainersRepo.findById(coursesDto.getTrainerId())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        course.setTrainer(trainer);
        Courses saved = coursesRepository.save(course);
        CoursesDto result = modelMapper.map(saved, CoursesDto.class);
        result.setTrainerId(saved.getTrainer().getId());
        return result;
    }

    @Override
    public CoursesDto getCourseById(int id) {
        Courses course = coursesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        CoursesDto dto = modelMapper.map(course, CoursesDto.class);
        dto.setTrainerId(course.getTrainer().getId());
        return dto;
    }

    @Override
    public CoursesDto updateCourse(int id, CoursesDto coursesDto) {
        Courses existing = coursesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        modelMapper.map(coursesDto, existing);
        if (coursesDto.getTrainerId() != 0) {
            Trainers trainer = trainersRepo.findById(coursesDto.getTrainerId())
                    .orElseThrow(() -> new RuntimeException("Trainer not found"));
            existing.setTrainer(trainer);
        }
        Courses updated = coursesRepository.save(existing);
        CoursesDto result = modelMapper.map(updated, CoursesDto.class);
        result.setTrainerId(updated.getTrainer().getId());
        return result;
    }

    @Override
    public void deleteCourse(int id) {
        if (!coursesRepository.existsById(id)) {
            throw new RuntimeException("Course not found");
        }
        coursesRepository.deleteById(id);
    }

    @Override
    public List<CoursesDto> getAllCourses() {
        return coursesRepository.findAll().stream()
                .map(course -> {
                    CoursesDto dto = modelMapper.map(course, CoursesDto.class);
                    dto.setTrainerId(course.getTrainer().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CoursesDto> getCoursesByTrainerId(int trainerId) {
        return coursesRepository.findByTrainer_Id(trainerId).stream()
                .map(course -> {
                    CoursesDto dto = modelMapper.map(course, CoursesDto.class);
                    dto.setTrainerId(course.getTrainer().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
