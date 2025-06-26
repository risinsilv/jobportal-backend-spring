package me.risinu.jobportal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    private int id;
    private String name;
    private String email;
    private String password;
    private String profilePic;
    private String role;
}