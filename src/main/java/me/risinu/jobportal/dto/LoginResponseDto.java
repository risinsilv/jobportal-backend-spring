package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private int id;
    private String name;
    private String role;
    private String token;
}