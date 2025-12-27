package me.risinu.jobportal.service.impl;

import me.risinu.jobportal.entity.EmailOTP;
import me.risinu.jobportal.entity.Users;
import me.risinu.jobportal.repo.EmailOTPRepo;
import me.risinu.jobportal.repo.UsersRepo;
import me.risinu.jobportal.service.EmailOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class EmailOTPServiceImpl implements EmailOTPService {

    private static final int OTP_LENGTH = 6;
    private static final int EXPIRY_MINUTES = 10;

    private final EmailOTPRepo emailOTPRepo;
    private final UsersRepo usersRepo;

    /**
     * Optional: only works if spring-boot-starter-mail is on the classpath and configured.
     */
    @Nullable
    private final JavaMailSender mailSender;

    private final SecureRandom secureRandom = new SecureRandom();

    @Autowired
    public EmailOTPServiceImpl(EmailOTPRepo emailOTPRepo, UsersRepo usersRepo, @Autowired(required = false) JavaMailSender mailSender) {
        this.emailOTPRepo = emailOTPRepo;
        this.usersRepo = usersRepo;
        this.mailSender = mailSender;
    }

    @Override
    public void sendOTP(int userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Delete any previous OTPs for this user before creating a new one.
        emailOTPRepo.deleteByUser(user);
        String otp = generateNumericOtp(OTP_LENGTH);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);

        EmailOTP emailOtp = emailOTPRepo.findByUser(user).orElseGet(EmailOTP::new);
        emailOtp.setUser(user);
        emailOtp.setOtp(otp);
        emailOtp.setExpirationDate(expiresAt);
        emailOTPRepo.save(emailOtp);

        // Send email if configured; otherwise do nothing.
        if (mailSender != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Your verification code");
            message.setText("Your OTP is: " + otp + "\n\nThis code expires in " + EXPIRY_MINUTES + " minutes.");
            mailSender.send(message);
        }
    }

    @Override
    public boolean verifyOTP(int userId, String otp) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        EmailOTP existing = emailOTPRepo.findByUser(user).orElse(null);
        if (existing == null) {
            return false;
        }

        if (existing.getExpirationDate() == null || existing.getExpirationDate().isBefore(LocalDateTime.now())) {
            emailOTPRepo.delete(existing);
            return false;
        }

        boolean matches = existing.getOtp() != null && existing.getOtp().equals(otp);
        if (matches) {
            // One-time use
            emailOTPRepo.delete(existing);
        }

        return matches;
    }

    @Override
    public void clearOTP(int userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        emailOTPRepo.deleteByUser(user);
    }

    private String generateNumericOtp(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }
}

