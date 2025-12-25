package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepo extends JpaRepository<Employer, Integer> {
}