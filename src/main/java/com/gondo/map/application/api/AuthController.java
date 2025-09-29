package com.gondo.map.application.api;

import com.gondo.map.component.util.PasswordUtil;
import com.gondo.map.config.security.JwtTokenProvider;
import com.gondo.map.config.security.SecurityUser;
import com.gondo.map.domain.common.CommResponse;
import com.gondo.map.domain.member.record.MemberRecord;
import com.gondo.map.domain.member.record.ResetPasswordRequest;
import com.gondo.map.domain.member.record.SecurityMemberRecord;
import com.gondo.map.domain.member.service.MemberService;
import com.gondo.map.domain.member.service.PasswordResetService;
import com.gondo.map.domain.member.vo.ChangeUserPwVo;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
@RestController
public class AuthController {

    private final MemberService memberService;
    private final PasswordResetService resetService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    public CommResponse<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return CommResponse.response400(HttpStatus.UNAUTHORIZED);
        }

        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        SecurityMemberRecord securityMemberRecord = securityUser.getSecurityMemberRecord();

        return CommResponse.response200(securityMemberRecord);
    }

    @PostMapping("/login")
    public CommResponse<?> login(@RequestBody MemberRecord memberRecord, HttpServletResponse response) {
        try {
            MemberRecord findRecord = memberService.getMember(memberRecord.email());
            if (!PasswordUtil.matches(memberRecord.password(), findRecord.password())) {
                throw new NoResultException("아이디 또는 비밀번호가 잘못되었습니다.");
            }

            response.addHeader("Set-Cookie", jwtTokenProvider.getJwtRoleCookie(findRecord.role()).toString());
            response.addHeader("Set-Cookie", jwtTokenProvider.getJwtCookie(findRecord.email()).toString());
            response.addHeader("Set-Cookie", jwtTokenProvider.getJwtRefreshCookie(findRecord.email()).toString());

            return CommResponse.response200(true);
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public CommResponse<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        if (refreshToken == null) {
            return CommResponse.response401("Refresh token is empty");
        }

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return CommResponse.response401("Invalid refresh token");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);

        ResponseCookie newAccessCookie = jwtTokenProvider.getJwtCookie(userId);

        response.addHeader("Set-Cookie", newAccessCookie.toString());

        return CommResponse.response200("토큰이 갱신되었습니다.");
    }

    @PostMapping("/logout")
    public CommResponse<?> logOut(HttpServletResponse response) {
        try {

            // 비회원 모드로 설정
            ResponseCookie roleCookie = ResponseCookie.from("role", "VIEWER")
                    .path("/")
                    .secure(false)
                    .sameSite("Lax")
                    .maxAge(24 * 60 * 60) // 1일
//                    .httpOnly(true)
                    .build();
            ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                    .path("/")
                    .secure(false)
                    .sameSite("Lax")
                    .maxAge(0)  // 즉시 만료
//                    .httpOnly(true)
                    .build();
            ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                    .path("/")
                    .secure(true)
                    .sameSite("Lax")
                    .maxAge(0)  // 즉시 만료
//                    .httpOnly(true)
                    .build();

            response.addHeader("Set-Cookie", roleCookie.toString());
            response.addHeader("Set-Cookie", accessCookie.toString());
            response.addHeader("Set-Cookie", refreshCookie.toString());
            return CommResponse.response200(true);
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/forgot-pw")
    public CommResponse<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            resetService.createAndSendResetToken(email);
            return CommResponse.response200(true);
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/reset-pw")
    public CommResponse<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            resetService.resetPassword(req.token(), req.newPassword());
            return CommResponse.response200(true);
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PatchMapping("/change-pw")
    public CommResponse<?> changePassword(@RequestBody ChangeUserPwVo reqVo) {
        try {
            return CommResponse.response200(memberService.changePassword(reqVo));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

}
