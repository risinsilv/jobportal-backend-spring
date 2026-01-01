package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.JobPostings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobPostingsRepo extends JpaRepository<JobPostings, Integer>, JpaSpecificationExecutor<JobPostings> {
    Page<JobPostings> findByEmployer_Id(int employerId, Pageable pageable);
}