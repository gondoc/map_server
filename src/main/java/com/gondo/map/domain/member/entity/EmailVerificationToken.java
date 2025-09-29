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
@Table(name = "tbl_email_verification_token", schema = "project")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "token")
    private String token;

    @Column(name = "target_email")
    private String targetEmail;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "create_dtm")
    private LocalDateTime createDtm;

    public EmailVerificationToken(String token, String memberId, String userEmail, LocalDateTime expiresAt) {
        this.token = token;
        this.memberId = memberId;
        this.targetEmail = userEmail;
        this.isVerified = false;
        this.expiresAt = expiresAt;
        this.createDtm = LocalDateTime.now();
    }

    public void setTokenUsed() {
        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
    }
}
