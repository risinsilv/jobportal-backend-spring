package me.risinu.jobportal.repo;


import me.risinu.jobportal.entity.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursesRepo extends JpaRepository<Courses, Integer> {
    List<Courses> findByTrainer_Id(int trainerId);
}
