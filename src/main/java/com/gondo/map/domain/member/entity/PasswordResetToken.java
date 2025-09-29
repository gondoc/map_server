package com.gondo.map.domain.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_password_reset_token", schema = "project")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String token; // UUID 등


    @Column(name = "user_email", nullable = false)
    private String userEmail;


    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;


    @Column(name = "used", nullable = false)
    private boolean used;

    public PasswordResetToken(String token, String userEmail, LocalDateTime expiresAt) {
        this.token = token;
        this.userEmail = userEmail;
        this.expiresAt = expiresAt;
    }

    public void setTokenUsed() {
        this.used = true;
    }
}
