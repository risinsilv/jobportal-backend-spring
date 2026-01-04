package me.risinu.jobportal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_match_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMatchDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    @JsonIgnore
    private Applications application;

    // 0..1 or 0..100 depending on upstream; store as Double and keep as-is
    @Column(name = "similarity_score")
    private Double similarityScore;

    @Column(name = "overall_match_level", length = 32)
    private String overallMatchLevel; // Weak | Moderate | Strong

    /**
     * Full analysis payload persisted as JSON.
     * Compatible with Hibernate 6 (Spring Boot 3+) using @JdbcTypeCode.
     * Falls back to LONGTEXT on dialects that don't support a JSON type.
     */
    @Lob
    @Column(name = "analysis_json", columnDefinition = "LONGTEXT")
    private String analysisJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

