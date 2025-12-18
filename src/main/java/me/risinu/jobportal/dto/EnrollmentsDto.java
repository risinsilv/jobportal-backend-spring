package me.risinu.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentsDto {
    private int enrollmentId;
    private int courseId;
    private int userId;
}