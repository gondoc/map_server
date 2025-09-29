package com.gondo.map.domain.member.entity;

import com.gondo.map.component.util.DateTimeUtil;
import com.gondo.map.component.util.EmailUtil;
import com.gondo.map.domain.member.record.MemberRecord;
import com.gondo.map.domain.member.record.SecurityMemberRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_member_info", schema = "project")
public class Member {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_email")
    private String email;

    @Column(name = "user_pw")
    private String password;

    @Column(name = "user_nick")
    private String nickName;

    @Column(name = "user_create_dtm")
    private String createAt;

    @Column(name = "user_update_dtm")
    private String updateAt;

    @Column(name = "user_recovery_email")
    private String recoveryEmail;

    @Column(name = "user_role")
    private String role;

    @Column(name = "user_recovery_email_verified")
    private boolean isRecoveryVerified;

    public MemberRecord toMemberRecord() {
//        return MemberRecord.builder()
//                .id(id)
//                .email(email)
//                .password(password)
//                .nickName(nickName)
//                .createAt(createAt)
//                .updateAt(updateAt)
//                .updateAt(recoveryEmail)
//                .build();
        return new MemberRecord(id, email, password, nickName, createAt, updateAt, recoveryEmail, role, isRecoveryVerified);
    }

    public void setNewPassword(String password) {
        this.password = password;
        this.updateAt = LocalDateTime.now().format(DateTimeUtil.getDefaultFormatter());
    }

    public void setNewNick(String nick) {
        this.nickName = nick;
        this.updateAt = LocalDateTime.now().format(DateTimeUtil.getDefaultFormatter());
    }

    public void registerRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public void verifyRecoveryEmail() {
        this.isRecoveryVerified = true;
    }

    public SecurityMemberRecord toSecurityMemberRecord() {
        return new SecurityMemberRecord(
                email,
                nickName,
                createAt,
                updateAt,
                EmailUtil.maskingEmail(recoveryEmail),
                role,
                isRecoveryVerified
        );
    }

}
