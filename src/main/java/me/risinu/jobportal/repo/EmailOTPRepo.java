package me.risinu.jobportal.repo;

import me.risinu.jobportal.entity.EmailOTP;
import me.risinu.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailOTPRepo extends JpaRepository<EmailOTP, Long> {
    Optional<EmailOTP> findByUser(Users user);
    void deleteByUser(Users user);
}

