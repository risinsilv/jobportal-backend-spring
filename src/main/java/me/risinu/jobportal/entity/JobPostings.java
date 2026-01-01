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
    @JoinColumn(name = "employer_id",referencedColumnName = "user_id")
    private Employer employer;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    /**
     * Legacy field kept to avoid breaking existing code.
     * Prefer using salaryMin/salaryMax/currency going forward.
     */
    private String salary;

    // --- New fields (not wired into DTO/service yet) ---

    @Column(name = "salary_min")
    private Integer salaryMin;

    @Column(name = "salary_max")
    private Integer salaryMax;

    /** ISO 4217 currency code (e.g., LKR, USD). */
    @Column(length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private WorkplaceType workplaceType;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String niceToHave;

    @Column(columnDefinition = "TEXT")
    private String other;

    public enum ExperienceLevel {
        INTERNSHIP,
        ENTRY_LEVEL,
        ASSOCIATE,
        MID_SENIOR_LEVEL,
        DIRECTOR,
        EXECUTIVE
    }

    public enum JobType {
        FULL_TIME,
        PART_TIME,
        CONTRACT,
        TEMPORARY,
        INTERN,
        FREELANCE
    }

    public enum WorkplaceType {
        ONSITE,
        HYBRID,
        REMOTE
    }

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
