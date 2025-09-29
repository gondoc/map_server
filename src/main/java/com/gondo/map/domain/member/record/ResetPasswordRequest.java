package com.gondo.map.domain.member.record;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}
