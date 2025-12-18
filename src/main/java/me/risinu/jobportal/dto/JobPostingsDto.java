package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingsDto {
    private int id;
    private int jobId;
    private int employerId;
    private String title;
    private String description;
    private String location;
    private String salary;
    private String requirements;
    private String status;
}