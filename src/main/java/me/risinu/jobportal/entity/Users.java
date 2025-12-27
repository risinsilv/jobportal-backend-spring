package me.risinu.jobportal.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String profilePic;

    @Enumerated(EnumType.STRING)
    private Role role;

    // default to false for newly created users
    @Column(nullable = false)
    private boolean isVerified = false;

    private LocalDateTime createdAt;

    public enum Role {
        JobSeeker, Employer
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // isVerified defaults to false via field initializer
    }
}
