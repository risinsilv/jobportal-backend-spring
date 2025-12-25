package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.JobSeekers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSeekersRepo extends JpaRepository<JobSeekers, Integer> {
}
