package me.risinu.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobId;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Users employer;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private String salary;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    private LocalDateTime postedAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        Open, Closed
    }

    @PrePersist
    protected void onCreate() {
        this.postedAt = LocalDateTime.now();
    }
}
