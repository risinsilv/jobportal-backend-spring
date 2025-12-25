package me.risinu.jobportal.service;
import me.risinu.jobportal.dto.CoursesDto;
import java.util.List;

public interface CoursesService {
    CoursesDto createCourse(CoursesDto coursesDto);
    CoursesDto getCourseById(int id);
    CoursesDto updateCourse(int id, CoursesDto coursesDto);
    void deleteCourse(int id);
    List<CoursesDto> getAllCourses();
    List<CoursesDto> getCoursesByTrainerId(int trainerId);
}
