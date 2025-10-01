package com.gondo.map.domain.member.service;

import com.gondo.map.component.MailSender;
import com.gondo.map.domain.member.dto.RegRecoveryMailReqDto;
import com.gondo.map.domain.member.entity.EmailVerificationToken;
import com.gondo.map.domain.member.entity.Member;
import com.gondo.map.domain.member.repository.EmailVerificationTokenRepository;
import com.gondo.map.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecoveryEmailService {
    private final MemberRepository memberRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final MailSender mailSender;
    private final static String VERIFY_RECOVERY_EMAIL_SUBJECT = "Career Map Back Office 복구용 이메일주소 인증 이메일 발송";

    @Value("${app.front-end-url}")
    private String frontendBaseUrl;

    public boolean regRecoveryMail(String email, String recoveryEmail) throws Exception {
        // 아디 기본 검증
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user by email"));

        member.registerRecoveryEmail(recoveryEmail);
        memberRepository.save(member);

        // 토큰 생성 및 메일 발송
        String token = UUID.randomUUID().toString();
        LocalDateTime accessTokenTime = LocalDateTime.now().plusDays(1);
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, member.getId(), recoveryEmail, accessTokenTime);
        emailVerificationTokenRepository.save(verificationToken);

        // 이메일 내용
        String verifyLink = frontendBaseUrl + "/back/verify-recovery-email?token=" + token;

        mailSender.mailSend(recoveryEmail, VERIFY_RECOVERY_EMAIL_SUBJECT, getVerifyRecoveryEmailContent(verifyLink));
        return true;
    }

    public boolean verifyMail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("No verification token found"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("이미 만료된 토큰입니다.");
        }

        if (verificationToken.getIsVerified()) {
            throw new RuntimeException("이미 인증된 토큰입니다.");
        }

        Member member = memberRepository.findById(verificationToken.getMemberId())
                .orElseThrow(() -> new NoSuchElementException("No member found"));

        member.verifyRecoveryEmail();
        memberRepository.save(member);

        verificationToken.setTokenUsed();
        emailVerificationTokenRepository.save(verificationToken);

        return true;
    }

    public static String getVerifyRecoveryEmailContent(String verifyLink) {
        StringBuilder sb = new StringBuilder();
        // 전체 메일 컨테이너
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // 헤더
        sb.append("<div style='text-align: center; margin-bottom: 30px;'>");
        sb.append("<h2 style='color: #333; margin: 0;'>복구용 이메일 인증</h2>");
        sb.append("</div>");

        // 메인 콘텐츠
        sb.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 8px; border: 1px solid #ddd;'>");

        // 안내 메시지
        sb.append("<p style='font-size: 16px; line-height: 1.6; color: #555; margin-bottom: 25px;'>");
        sb.append("안녕하세요!<br>");
        sb.append("계정 복구용 이메일 주소로 등록을 요청하셨습니다.<br>");
        sb.append("아래 버튼을 클릭하여 이메일 인증을 완료해주세요.");
        sb.append("</p>");

        // 인증 버튼 - 실제 토큰 링크로 교체 필요
        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<a href='").append(verifyLink).append("' style='background-color: #769FCD; ");
        sb.append("color: #FFFFFF; padding: 18px 40px; text-decoration: none; ");
        sb.append("border-radius: 8px; font-size: 16px; font-weight: bold; ");
        sb.append("display: inline-block; box-shadow: 0 4px 15px rgba(0,0,0,0.2); ");
        sb.append("transition: background-color 0.3s;'>");
        sb.append("이메일 인증하기");
        sb.append("</a>");
        sb.append("</div>");

        // 버튼이 작동하지 않을 경우 안내
        sb.append("<div style='background-color: #f8f9fa; border: 1px solid #dee2e6; ");
        sb.append("border-radius: 4px; padding: 15px; margin: 25px 0;'>");
        sb.append("<p style='margin: 0; font-size: 13px; color: #666; line-height: 1.5;'>");
        sb.append("<strong>버튼이 작동하지 않는 경우:</strong><br>");
        sb.append("아래 링크를 복사하여 브라우저 주소창에 직접 붙여넣기 해주세요.<br>");
        sb.append("<span style='word-break: break-all; color: #769FCD; font-family: monospace;'>");
        sb.append(verifyLink);
        sb.append("</span>");
        sb.append("</p>");
        sb.append("</div>");

        // 유효시간 안내
        sb.append("<div style='background-color: #fff3cd; border: 1px solid #ffeaa7; ");
        sb.append("border-radius: 4px; padding: 15px; margin: 25px 0;'>");
        sb.append("<p style='margin: 0; font-size: 14px; color: #856404;'>");
        sb.append("⏰ <strong>중요:</strong> 이 인증 링크는 <strong>24시간</strong> 후 만료됩니다.<br>");
        sb.append("만료 전에 꼭 인증을 완료해주세요.");
        sb.append("</p>");
        sb.append("</div>");

        // 주의사항
        sb.append("<div style='margin-top: 25px; padding-top: 20px; border-top: 1px solid #ddd;'>");
        sb.append("<p style='font-size: 13px; color: #777; margin: 0; line-height: 1.5;'>");
        sb.append("<strong>보안을 위한 주의사항:</strong><br>");
        sb.append("• 본인이 요청하지 않은 인증 메일이라면 무시하시기 바랍니다<br>");
        sb.append("• 이 링크는 타인과 공유하지 마세요<br>");
        sb.append("• 인증 완료 후 이 이메일 주소로 계정 복구가 가능합니다");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 메인 콘텐츠 끝

        // 푸터
        sb.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; ");
        sb.append("border-top: 1px solid #eee;'>");
        sb.append("<p style='font-size: 12px; color: #999; margin: 0;'>");
        sb.append("본 메일은 복구용 이메일 등록 요청에 의해 자동으로 발송된 메일입니다.<br>");
        sb.append("인증을 완료하면 이 이메일로 아이디 찾기 및 비밀번호 재설정이 가능합니다.");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 전체 컨테이너 끝
        return sb.toString();
    }
}
