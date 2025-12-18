package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.dto.EnrollmentsDto;
import me.risinu.jobportal.entity.Courses;
import me.risinu.jobportal.entity.Enrollments;
import me.risinu.jobportal.entity.Users;

import me.risinu.jobportal.repo.CoursesRepo;
import me.risinu.jobportal.repo.EnrollmentsRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.EnrollmentsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentsServiceImpl implements EnrollmentsService {

    @Autowired
    private EnrollmentsRepo enrollmentsRepository;

    @Autowired
    private CoursesRepo coursesRepository;

    @Autowired
    private UsersRepo usersRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public EnrollmentsDto createEnrollment(EnrollmentsDto dto) {
        Enrollments enrollment = new Enrollments();
        Courses course = coursesRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Users user = usersRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        enrollment.setCourse(course);
        enrollment.setUser(user);
        Enrollments saved = enrollmentsRepository.save(enrollment);
        EnrollmentsDto result = modelMapper.map(saved, EnrollmentsDto.class);
        result.setCourseId(saved.getCourse().getCourseId());
        result.setUserId(saved.getUser().getId());
        return result;
    }

    @Override
    public EnrollmentsDto getEnrollmentById(int id) {
        Enrollments enrollment = enrollmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        EnrollmentsDto dto = modelMapper.map(enrollment, EnrollmentsDto.class);
        dto.setCourseId(enrollment.getCourse().getCourseId());
        dto.setUserId(enrollment.getUser().getId());

        return dto;
    }

    @Override
    public EnrollmentsDto updateEnrollment(int id, EnrollmentsDto dto) {
        Enrollments existing = enrollmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        if (dto.getCourseId() != 0) {
            Courses course = coursesRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            existing.setCourse(course);
        }
        if (dto.getUserId() != 0) {
            Users user = usersRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            existing.setUser(user);
        }
        Enrollments updated = enrollmentsRepository.save(existing);
        EnrollmentsDto result = modelMapper.map(updated, EnrollmentsDto.class);
        result.setCourseId(updated.getCourse().getCourseId());
        result.setUserId(updated.getUser().getId());
        return result;
    }

    @Override
    public void deleteEnrollment(int id) {
        if (!enrollmentsRepository.existsById(id)) {
            throw new RuntimeException("Enrollment not found");
        }
        enrollmentsRepository.deleteById(id);
    }

    @Override
    public List<EnrollmentsDto> getAllEnrollments() {
        return enrollmentsRepository.findAll().stream()
                .map(enrollment -> {
                    EnrollmentsDto dto = modelMapper.map(enrollment, EnrollmentsDto.class);
                    dto.setCourseId(enrollment.getCourse().getCourseId());
                    dto.setUserId(enrollment.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentsDto> getEnrollmentsByUserId(int userId) {
        return enrollmentsRepository.findByUser_Id(userId).stream()
                .map(enrollment -> {
                    EnrollmentsDto dto = modelMapper.map(enrollment, EnrollmentsDto.class);
                    dto.setCourseId(enrollment.getCourse().getCourseId());
                    dto.setUserId(enrollment.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentsDto> getEnrollmentsByCourseId(int courseId) {
        return enrollmentsRepository.findByCourse_CourseId(courseId).stream()
                .map(enrollment -> {
                    EnrollmentsDto dto = modelMapper.map(enrollment, EnrollmentsDto.class);
                    dto.setCourseId(enrollment.getCourse().getCourseId());
                    dto.setUserId(enrollment.getUser().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}