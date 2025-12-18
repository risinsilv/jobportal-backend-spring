package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.JobPostings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingsRepo extends JpaRepository<JobPostings, Integer> {
}