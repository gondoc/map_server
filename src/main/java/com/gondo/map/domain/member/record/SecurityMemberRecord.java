package com.gondo.map.domain.member.record;

public record SecurityMemberRecord(
        String email,
        String nick,
        String createAt,
        String updateAt,
        String recoveryEmail,
        String role,
        boolean isVerifyRcvryEmail
) {
}
