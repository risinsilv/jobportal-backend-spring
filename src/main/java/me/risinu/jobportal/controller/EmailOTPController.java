package me.risinu.jobportal.controller;

import me.risinu.jobportal.dto.OtpStatusResponseDto;
import me.risinu.jobportal.dto.SendOtpRequestDto;
import me.risinu.jobportal.dto.VerifyOtpRequestDto;
import me.risinu.jobportal.service.EmailOTPService;
import me.risinu.jobportal.service.UsersService;
import me.risinu.jobportal.util.JWT;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-otp")
public class EmailOTPController {

    private final EmailOTPService emailOTPService;
    private final UsersService usersService;
    private final JWT jwt;

    public EmailOTPController(EmailOTPService emailOTPService, UsersService usersService, JWT jwt) {
        this.emailOTPService = emailOTPService;
        this.usersService = usersService;
        this.jwt = jwt;
    }

    /**
     * Sends (or re-sends) an OTP to the user's email.
     * body: { "userId": 123 }
     */
    @PostMapping("/send")
    public ResponseEntity<OtpStatusResponseDto> send(
            @RequestHeader("Authorization") String token,
            @RequestBody SendOtpRequestDto body
    ) {
        if (body == null || body.getUserId() <= 0) {
            return ResponseEntity.badRequest().body(new OtpStatusResponseDto(false, "userId is required"));
        }
        if (!jwt.verifyTokenOwnedByUser(token, body.getUserId())) {
            return ResponseEntity.status(403).body(new OtpStatusResponseDto(false, "Forbidden"));
        }

        // Only send OTP if email is NOT verified yet.
        if (usersService.isEmailVerified(body.getUserId())) {
            return ResponseEntity.status(409).body(new OtpStatusResponseDto(false, "Email is already verified"));
        }

        emailOTPService.sendOTP(body.getUserId());
        return ResponseEntity.ok(new OtpStatusResponseDto(true, "OTP sent"));
    }

    /**
     * Verifies the OTP.
     * body: { "userId": 123, "otp": "123456" }
     */
    @PostMapping("/verify")
    public ResponseEntity<OtpStatusResponseDto> verify(
            @RequestHeader("Authorization") String token,
            @RequestBody VerifyOtpRequestDto body
    ) {
        if (body == null || body.getUserId() <= 0) {
            return ResponseEntity.badRequest().body(new OtpStatusResponseDto(false, "userId is required"));
        }
        if (!jwt.verifyTokenOwnedByUser(token, body.getUserId())) {
            return ResponseEntity.status(403).body(new OtpStatusResponseDto(false, "Forbidden"));
        }
        if (body.getOtp() == null || body.getOtp().isBlank()) {
            return ResponseEntity.badRequest().body(new OtpStatusResponseDto(false, "otp is required"));
        }

        boolean ok = emailOTPService.verifyOTP(body.getUserId(), body.getOtp());
        if (!ok) {
            return ResponseEntity.status(400).body(new OtpStatusResponseDto(false, "Invalid or expired OTP"));
        }

        usersService.markEmailVerified(body.getUserId());
        return ResponseEntity.ok(new OtpStatusResponseDto(true, "Email verified"));
    }

    /**
     * Optional cleanup endpoint.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> clear(@PathVariable int userId) {
        emailOTPService.clearOTP(userId);
        return ResponseEntity.noContent().build();
    }
}
