package me.risinu.jobportal.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentsRepo extends JpaRepository<Enrollments, Integer> {
    List<Enrollments> findByUser_Id(int userId);
    List<Enrollments> findByCourse_CourseId(int courseId);
}
