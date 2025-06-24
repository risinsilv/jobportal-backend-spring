package me.risinu.jobportal.controller;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trainers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainers {
    @Id
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Users user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String specialization;

    private String company;

    @Column(columnDefinition = "TEXT")
    private String certifications;

    private LocalDateTime createdAt;
}
