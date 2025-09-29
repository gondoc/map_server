package com.gondo.map.component;

import com.gondo.map.component.util.EmailUtil;
import com.gondo.map.component.util.RandomUtil;
import com.gondo.map.component.util.RedisUtils;
import com.google.common.base.Strings;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailSender {

    private final RedisUtils redisUtils;
    private final RandomUtil randomUtil;
    private final JavaMailSender javaMailSender;
    private final static long VERIFY_CD_EXPIRE = 10; // 10분 설정
    private final static String JOIN_MAIL_VERIFY_SUBJECT = "Career Map Back Office 회원가입 이메일 인증코드 발송";
    private final static String FIND_ID_SUBJECT = "Career Map Back Office 아이디 찾기 이메일 발송";

    // 이메일 인증코드 발송
    public long sendVerificationCode(String email) {
        try {
            // 이메일을 받은 후 검증 로직 작성요망
            Optional<String> verifyCd = redisUtils.get(email, String.class);

            // 코드가 있는 경우
            if (verifyCd.isPresent()) {
                // 아이템 삭제
                redisUtils.delete(email);
            }

            // 없는 경우 발행
            return issuanceVerifyCdSendMail(email);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // 발송 처리 담당 메서드 (코드 발행, 메일 발송, email 과 코드 저장, 만료 시간 반환)
    private long issuanceVerifyCdSendMail(String email) throws Exception {
        String verifyCd = issuanceVerifyCd();
        long expireEpoch = saveVerifyCd(email, verifyCd);
        mailSend(email, JOIN_MAIL_VERIFY_SUBJECT, getVerifyMailContent(verifyCd, expireEpoch));
        return expireEpoch;
    }

    // 메일 전송 메서드
    public void mailSend(String email, String subject, String content) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
        message.setSubject(subject);
        message.setContent(content, "text/html;charset=UTF-8");
        javaMailSender.send(message);
    }

    // 메일 본문을 만드는 메서드
    private static String getVerifyMailContent(String verifyCd, long expireEpoch) {
        StringBuilder sb = new StringBuilder();

        // 현재 시간과 만료시간 계산
        long currentTime = System.currentTimeMillis();
        long remainingMinutes = (expireEpoch - currentTime) / (1000 * 60);
        String expiryText = remainingMinutes > 0 ? remainingMinutes + "분" : "곧 만료";

        // 전체 메일 컨테이너
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // 헤더
        sb.append("<div style='text-align: center; margin-bottom: 30px;'>");
        sb.append("<h2 style='color: #333; margin: 0;'>이메일 인증</h2>");
        sb.append("</div>");

        // 메인 콘텐츠
        sb.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 8px; border: 1px solid #ddd;'>");

        // 안내 메시지
        sb.append("<p style='font-size: 16px; line-height: 1.6; color: #555; margin-bottom: 25px;'>");
        sb.append("안녕하세요!<br>");
        sb.append("회원가입을 완료하기 위해 이메일 인증이 필요합니다.<br>");
        sb.append("아래의 6자리 인증코드를 화면에 입력해주세요.");
        sb.append("</p>");

        // 인증코드 박스 - 브랜드 컬러 적용
        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<div style='background-color: #769FCD; ");
        sb.append("color: #FFFFFF; padding: 25px 20px; border-radius: 10px; ");
        sb.append("display: inline-block; box-shadow: 0 4px 15px rgba(0,0,0,0.2);'>");
        sb.append("<p style='margin: 0 0 10px 0; font-size: 14px; opacity: 0.9;'>인증코드</p>");
        sb.append("<div style='font-size: 36px; font-weight: bold; letter-spacing: 8px; ");
        sb.append("font-family: monospace;'>").append(verifyCd).append("</div>");
        sb.append("</div>");
        sb.append("</div>");

        // 유효시간 안내 - 브랜드 컬러 적용하고 동적 시간 표시
        sb.append("<div style='background-color: #e7f3ff; border: 1px solid #93B7E4; ");
        sb.append("border-radius: 4px; padding: 15px; margin: 25px 0;'>");
        sb.append("<p style='margin: 0; font-size: 14px; color: #769FCD;'>");
        sb.append("ℹ️ <strong>안내:</strong> 이 인증코드는 <strong>").append(expiryText).append("</strong> 후 만료됩니다.");
        sb.append("</p>");
        sb.append("</div>");

        // 주의사항
        sb.append("<div style='margin-top: 25px; padding-top: 20px; border-top: 1px solid #ddd;'>");
        sb.append("<p style='font-size: 13px; color: #777; margin: 0; line-height: 1.5;'>");
        sb.append("<strong>보안을 위한 주의사항:</strong><br>");
        sb.append("• 이 인증코드는 타인에게 공유하지 마세요<br>");
        sb.append("• 본인이 요청하지 않은 인증코드라면 무시하시기 바랍니다");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 메인 콘텐츠 끝

        // 푸터
        sb.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; ");
        sb.append("border-top: 1px solid #eee;'>");
        sb.append("<p style='font-size: 12px; color: #999; margin: 0;'>");
        sb.append("본 메일은 이메일 인증을 위해 자동으로 발송된 메일입니다.<br>");
        sb.append("회원가입을 완료하려면 위 인증코드를 입력해주세요.");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 전체 컨테이너 끝

        return sb.toString();
    }

    // 인증 코드 발행
    private String issuanceVerifyCd() {
        return String.valueOf(randomUtil.getRandomIntValuesByLength(6));
    }

    // 이메일주소와 인증코드를 저장
    private long saveVerifyCd(String email, String verifyCd) {
        // 만료시간
        Duration duration = Duration.ofMinutes(VERIFY_CD_EXPIRE);
        long epochTime = parseDurationToEpochTime(duration);
        redisUtils.set(email, verifyCd, duration);
        return epochTime;
    }

    // 시간을 epochTime 으로 변환
    private long parseDurationToEpochTime(Duration expireDuration) {
        return Instant.now().plus(expireDuration).toEpochMilli();
    }

    //----- 아이디 찾기(접속용 이메일 계정 찾기)
    public boolean sendRecoveryEmail(String recoveryEmail, String email) throws Exception {
        String maskedEmail = EmailUtil.maskingEmail(email);
        if (Strings.isNullOrEmpty(maskedEmail)) {
            return false;
        }

        mailSend(recoveryEmail, FIND_ID_SUBJECT, getFindIdNoticeContent(maskedEmail));
        return true;
    }

    private static String getFindIdNoticeContent(String maskedEmail) {
        StringBuilder sb = new StringBuilder();
        // 전체 메일 컨테이너
        sb.append("<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;'>");

        // 헤더
        sb.append("<div style='text-align: center; margin-bottom: 30px;'>");
        sb.append("<h2 style='color: #333; margin: 0;'>아이디 찾기 결과</h2>");
        sb.append("</div>");

        // 메인 콘텐츠
        sb.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 8px; border: 1px solid #ddd;'>");

        // 안내 메시지
        sb.append("<p style='font-size: 16px; line-height: 1.6; color: #555; margin-bottom: 25px; text-align: center;'>");
        sb.append("아이디 찾기 요청이 완료되었습니다.<br>");
        sb.append("입력하신 정보로 확인된 이메일 주소는 아래와 같습니다.");
        sb.append("</p>");

        // 찾은 이메일 주소 박스
        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<div style='background-color: #769FCD; ");
        sb.append("color: #FFFFFF; padding: 25px 20px; border-radius: 10px; ");
        sb.append("display: inline-block; box-shadow: 0 4px 15px rgba(0,0,0,0.2);'>");
        sb.append("<p style='margin: 0 0 10px 0; font-size: 14px; opacity: 0.9;'>등록된 이메일 주소</p>");
        sb.append("<div style='font-size: 24px; font-weight: bold; letter-spacing: 2px; ");
        sb.append("font-family: monospace;'>").append(maskedEmail).append("</div>");
        sb.append("</div>");
        sb.append("</div>");

        // 다음 단계 안내
        sb.append("<div style='background-color: #e7f3ff; border: 1px solid #93B7E4; ");
        sb.append("border-radius: 4px; padding: 20px; margin: 25px 0;'>");
        sb.append("<h4 style='margin: 0 0 10px 0; font-size: 16px; color: #769FCD;'>");
        sb.append("- 다음 단계</h4>");
        sb.append("<p style='margin: 0; font-size: 14px; color: #555; line-height: 1.5;'>");
        sb.append("• 위 이메일 주소로 로그인을 시도해보세요<br>");
        sb.append("• 비밀번호가 기억나지 않는 경우 비밀번호 재설정을 이용하세요<br>");
        sb.append("</p>");
        sb.append("</div>");

        // 보안 안내
        sb.append("<div style='background-color: #fff9e6; border: 1px solid #93B7E4; ");
        sb.append("border-radius: 4px; padding: 15px; margin: 20px 0;'>");
        sb.append("<p style='margin: 0; font-size: 13px; color: #769FCD;'>");
        sb.append("- <strong>보안 안내:</strong> 개인정보 보호를 위해 이메일 주소의 일부를 마스킹하여 표시했습니다.");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 메인 콘텐츠 끝

        // 푸터
        sb.append("<div style='text-align: center; margin-top: 30px; padding-top: 20px; ");
        sb.append("border-top: 1px solid #eee;'>");
        sb.append("<p style='font-size: 12px; color: #999; margin: 0;'>");
        sb.append("본 메일은 아이디 찾기 요청에 의해 자동으로 발송된 메일입니다.<br>");
        sb.append("만약 요청하지 않으셨다면 이 메일을 무시하시기 바랍니다.");
        sb.append("</p>");
        sb.append("</div>");

        sb.append("</div>"); // 전체 컨테이너 끝
        return sb.toString();
    }

}
