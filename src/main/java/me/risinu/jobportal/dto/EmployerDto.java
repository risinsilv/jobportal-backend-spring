package me.risinu.jobportal.dto;

import lombok.Data;

@Data
public class EmployerDto {
    private int id;
    private int userId;
    private String companyName;
    private String companyWebsite;
    private String companyAddress;
    private String contactInfo;
    private String position;
}
