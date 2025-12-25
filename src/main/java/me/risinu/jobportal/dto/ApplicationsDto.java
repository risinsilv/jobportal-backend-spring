package me.risinu.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApplicationsDto {
    private int applicationId;
    private int jobId;
    private int jobSeekerId;
    private String status;
}