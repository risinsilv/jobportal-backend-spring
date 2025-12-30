package me.risinu.jobportal.service;

import me.risinu.jobportal.dto.GoogleUserInfoDto;

public interface GoogleOAuthService {
    /**
     * Validates a Google ID token (JWT) via Google tokeninfo endpoint and returns the user profile.
     */
    GoogleUserInfoDto validateIdTokenAndGetUser(String idToken);
}
