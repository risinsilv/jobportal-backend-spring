package me.risinu.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrainersDto {
    private int id;
    private int userId;
    private String bio;
    private String specialization;
    private String company;
    private String certifications;
}
