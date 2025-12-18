package me.risinu.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Applications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int applicationId;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private JobPostings job;

    @ManyToOne
    @JoinColumn(name = "jobseeker_id", referencedColumnName = "user_id")
    private JobSeekers jobSeeker;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime appliedAt;

    public enum Status {
        Applied, Shortlisted, Rejected, Hired

    }
    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
    }
}
