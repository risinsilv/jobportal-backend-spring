package me.risinu.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Frontend sends Google ID token (JWT) obtained from Google Identity Services.
 * Optionally sends role ONLY for first-time sign up.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequestDto {
    /** Google ID token (JWT). */
    private String idToken;
    /** Optional. Used only if user does not already exist. */
    private String role;
}
