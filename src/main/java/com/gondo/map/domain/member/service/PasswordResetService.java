package com.gondo.map.domain.member.service;

import com.gondo.map.component.MailSender;
import com.gondo.map.component.util.PasswordUtil;
import com.gondo.map.domain.member.entity.Member;
import com.gondo.map.domain.member.entity.PasswordResetToken;
import com.gondo.map.domain.member.repository.MemberRepository;
import com.gondo.map.domain.member.repository.PasswordResetTokenRepository;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository resetTokenRepository;
    private final MemberRepository memberRepository;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    private final static String FIND_PW_RECOVERY_EMAIL_SUBJECT = "Career Map Back Office 비밀번호 재설정 안내";

    @Value("${app.front-end-url}")
    private String frontendBaseUrl;

    // 토큰 생성 및 이메일 발송
    public void createAndSendResetToken(String email) throws Exception {
        if (Strings.isNullOrEmpty(email)) {
            throw new UsernameNotFoundException("No user by email");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user by email"));

        String token = UUID.randomUUID().toString();
        LocalDateTime accessTokenTime = LocalDateTime.now().plusMinutes(30);
        PasswordResetToken prt = new PasswordResetToken(token, member.getEmail(), accessTokenTime);
        resetTokenRepository.save(prt);

        // 이메일 내용
        String resetLink = frontendBaseUrl + "/back/reset?token=" + token;
        String time = accessTokenTime.format(DateTimeFormatter.ofPattern("HH시 mm분"));
        mailSender.mailSend(member.getEmail(),
                FIND_PW_RECOVERY_EMAIL_SUBJECT,
                makeResetLinkMailContent(resetLink, time)
        );
    }

    // 비밀번호 변경
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = validateToken(token);
        Member member = memberRepository.findByEmail(resetToken.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        member.setNewPassword(PasswordUtil.encrypt(newPassword));
        memberRepository.save(member);

        // 토큰 사용 처리
        resetToken.setTokenUsed();
        resetTokenRepository.save(resetToken);

        // 추가: 기존 세션/토큰 무효화 로직 (예: 회원의 refresh token 제거 등) 수행
    }

    // 토큰 검증
    private PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        if (resetToken.isUsed()) throw new IllegalArgumentException("Token already used");
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Token expired");
        return resetToken;
    }

    private String makeResetLinkMailContent(String resetLink, String time) {
        StringBuilder sb = new StringBuilder();

        // 전체 메일 컨테이너
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // 헤더
        sb.append("<div style='text-align: center; margin-bottom: 30px;'>");
        sb.append("<h2 style='color: #333; margin: 0;'>비밀번호 재설정</h2>");
        sb.append("</div>");

        // 메인 콘텐츠
        sb.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 8px; border: 1px solid #ddd;'>");

        // 안내 메시지
        sb.append("<p style='font-size: 16px; line-height: 1.6; color: #555; margin-bottom: 25px;'>");
        sb.append("안녕하세요.<br>");
        sb.append("비밀번호 재설정을 요청하셨습니다.<br>");
        sb.append("아래 버튼을 클릭하여 새로운 비밀번호를 설정해주세요.");
        sb.append("</p>");

        // 재설정 버튼 - 브랜드 컬러 적용
        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<a href='").append(resetLink).append("' ");
        sb.append("style='display: inline-block; background-color: #769FCD; color: #FFFFFF; ");
        sb.append("padding: 15px 30px; text-decoration: none; border-radius: 5px; ");
        sb.append("font-size: 16px; font-weight: bold; transition: background-color 0.3s;'>");
        sb.append("비밀번호 재설정하기");
        sb.append("</a>");
        sb.append("</div>");

        // 유효시간 안내 - 브랜드 컬러 적용
        sb.append("<div style='background-color: #e7f3ff; border: 1px solid #93B7E4; ");
        sb.append("border-radius: 4px; padding: 15px; margin: 20px 0;'>");
        sb.append("<p style='margin: 0; font-size: 14px; color: #769FCD;'>");
        sb.append("⚠️ <strong>중요:</strong> 이 링크는 <strong>").append(time).append("</strong>까지 유효합니다.");
        sb.append("</p>");
        sb.append("</div>");

        // 링크가 작동하지 않는 경우
        sb.append("<div style='margin-top: 25px; padding-top: 20px; border-top: 1px solid #ddd;'>");
        sb.append("<p style='font-size: 13px; color: #777; margin-bottom: 10px;'>");
        sb.append("위 버튼이 작동하지 않는 경우, 아래 링크를 복사하여 브라우저에 직접 입력하세요:");
        sb.append("</p>");
        sb.append("<p style='word-break: break-all; background-color: #f1f1f1; padding: 10px; ");
        sb.append("border-radius: 4px; font-size: 12px; color: #333;'>");
        sb.append(resetLink);
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 메인 콘텐츠 끝

        // 푸터
        sb.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; ");
        sb.append("border-top: 1px solid #eee;'>");
        sb.append("<p style='font-size: 12px; color: #999; margin: 0;'>");
        sb.append("본 메일은 비밀번호 재설정 요청에 의해 자동으로 발송된 메일입니다.<br>");
        sb.append("만약 요청하지 않으셨다면 이 메일을 무시하시기 바랍니다.");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 전체 컨테이너 끝

        return sb.toString();
    }
}
