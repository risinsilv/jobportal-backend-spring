    package me.risinu.jobportal.service;

/**
 * Service for handling email-based OTP generation and verification.
 */
public interface EmailOTPService {

    /**
     * Generates (or regenerates) an OTP for the given user and sends it to the user's email.
     */
    void sendOTP(int userId);

    /**
     * Verifies the provided OTP for the given user.
     *
     * @return true if OTP is valid and not expired; false otherwise
     */
    boolean verifyOTP(int userId, String otp);

    /**
     * Removes any stored OTP for the given user.
     */
    void clearOTP(int userId);
}

