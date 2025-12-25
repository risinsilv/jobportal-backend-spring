package me.risinu.jobportal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSeekersDto {
    private int id;
    private int userId;
    private String title ;
    private String address;
    private String resumeUrl;
    private String profileSummary;
    private String skills;
    private String jobHistory;
    private String experience;
    private String certifications;
    private String contactInfo;
    private String education;
}
