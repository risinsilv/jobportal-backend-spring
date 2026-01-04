package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.Applications;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationsRepo extends JpaRepository<Applications, Integer> {
    List<Applications> findByJob_Employer_Id(int employerId);
    List<Applications> findByJobSeeker_Id(int jobSeekerId);
    List<Applications> findByJob_JobId(int jobId);
}
