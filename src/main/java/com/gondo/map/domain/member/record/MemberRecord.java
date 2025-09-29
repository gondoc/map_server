package com.gondo.map.domain.member.record;

import com.gondo.map.component.util.DateTimeUtil;
import com.gondo.map.component.util.PasswordUtil;
import com.gondo.map.domain.member.UserRole;
import com.gondo.map.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.UUID;

public record MemberRecord(
        String id,
        String email,
        String password,
        String nickName,
        String createAt,
        String updateAt,
        String recoveryEmail,
        String role,
        boolean isRecoveryVerified
) {

    public Member toMemberEntity() {
        return Member.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .password(PasswordUtil.encrypt(password))
                .nickName(nickName)
                .createAt(LocalDateTime.now().format(DateTimeUtil.getDefaultFormatter()))
                .recoveryEmail(recoveryEmail)
                .role(UserRole.MEMBER.name())
                .build();
    }
}
