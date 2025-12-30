package me.risinu.jobportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GoogleUserInfoDto {
    private String sub; // Google user id
    private String name;
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    private String picture;

    /** token audience (client id) */
    private String aud;
}
