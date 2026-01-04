package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.ApplicationMatchDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationMatchDetailsRepo extends JpaRepository<ApplicationMatchDetails, Long> {
    Optional<ApplicationMatchDetails> findByApplication_ApplicationId(int applicationId);

    java.util.List<ApplicationMatchDetails> findByApplication_Job_JobId(int jobId);
}
