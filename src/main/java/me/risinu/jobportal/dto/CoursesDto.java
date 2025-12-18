package me.risinu.jobportal.dto;

import lombok.Data;


@Data
public class CoursesDto {
    private int courseId;
    private int trainerId;
    private String title;
    private String description;
    private String cost;
    private String videoUrl;
}
