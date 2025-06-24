package me.risinu.jobportal.controller;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
    private JobPostings job;

    @ManyToOne
    @JoinColumn(name = "jobseeker_id")
    private Users jobSeeker;

    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime appliedAt;

    public enum Status {
        Applied, Shortlisted, Rejected, Hired
    }
}
