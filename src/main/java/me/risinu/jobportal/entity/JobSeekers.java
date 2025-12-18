package me.risinu.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_seekers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekers {
    @Id
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Users user;

    private String title;

    private String address;

    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String profileSummary;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(columnDefinition = "TEXT")
    private String jobHistory;

    @Column(columnDefinition = "TEXT")
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    @Column(columnDefinition = "TEXT")
    private String contactInfo;

    @Column(columnDefinition = "TEXT")
    private String education;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
