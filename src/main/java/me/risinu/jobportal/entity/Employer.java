package me.risinu.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "employer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employer {
    @Id
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Users user;

    private String companyName;

    private String companyWebsite;

    @Column(columnDefinition = "TEXT")
    private String companyAddress;

    private String contactInfo;

    private String companyLogoUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
