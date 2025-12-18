package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.EnrollmentsDto;
import java.util.List;

public interface EnrollmentsService {
    EnrollmentsDto createEnrollment(EnrollmentsDto enrollmentsDto);
    EnrollmentsDto getEnrollmentById(int id);
    EnrollmentsDto updateEnrollment(int id, EnrollmentsDto enrollmentsDto);
    void deleteEnrollment(int id);
    List<EnrollmentsDto> getAllEnrollments();
    List<EnrollmentsDto> getEnrollmentsByUserId(int userId);
    List<EnrollmentsDto> getEnrollmentsByCourseId(int courseId);
}
