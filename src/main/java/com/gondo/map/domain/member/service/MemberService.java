package com.gondo.map.domain.member.service;

import com.gondo.map.component.MailSender;
import com.gondo.map.component.util.EmailUtil;
import com.gondo.map.component.util.RandomUtil;
import com.gondo.map.component.util.RedisUtils;
import com.gondo.map.config.constant.KorAdjective;
import com.gondo.map.domain.member.dto.ChangeNickReqDto;
import com.gondo.map.domain.member.dto.RegRecoveryMailReqDto;
import com.gondo.map.domain.member.entity.Member;
import com.gondo.map.domain.member.record.MemberRecord;
import com.gondo.map.domain.member.record.VerifyRecord;
import com.gondo.map.domain.member.repository.MemberRepository;
import com.gondo.map.domain.member.vo.ChangeUserPwVo;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service("UserService")
public class MemberService {

    private final MemberRepository memberRepository;
    private final KorAdjective korAdjective;
    private final RandomUtil randomUtil;
    private final RedisUtils redisUtils;
    private final MailSender sender;

    public boolean signUp(MemberRecord dto) throws Exception {
        try {
            Member save = memberRepository.save(dto.toMemberEntity());
            log.info("User saved: {}", save);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean idCheck(String email) throws Exception {
        try {
            Optional<Member> byUserEmail = memberRepository.findByEmail(email);
            return byUserEmail.isEmpty();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean nickCheck(String nick) throws Exception {
        try {
            Optional<Member> byUserNick = memberRepository.findByNickName(nick);
            return byUserNick.isEmpty();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public boolean changeNick(ChangeNickReqDto changeNickReqDto) throws Exception {
        try {
            Member member = memberRepository.findByEmail(changeNickReqDto.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            member.setNewNick(changeNickReqDto.getNickName());
            memberRepository.save(member);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public MemberRecord getMember(String email) throws Exception {
        Optional<Member> opEntity = memberRepository.findByEmail(email);
        if (opEntity.isEmpty()) {
            throw new Exception("아이디 또는 비밀번호가 잘못되었습니다.");
        } else {
            return opEntity.get().toMemberRecord();
        }
    }

    public boolean validVerifyCode(VerifyRecord verifyRecord) {
        try {
            Optional<String> opVerifyCode = redisUtils.get(verifyRecord.email(), String.class);
            return opVerifyCode.map(cd -> cd.equals(verifyRecord.code())).orElse(false);
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public String sendRandomNick() {
        String adjective = korAdjective.findIndex(randomUtil.getRandomIntByMax(korAdjective.getLength()));
        String randomInt = String.valueOf(randomUtil.getRandomIntValuesByLength(4));
        return adjective.concat("유저").concat(randomInt);
    }

    public long sendVerifyCode(String email) {
        boolean valid = EmailUtil.isValid(email);
        if (!valid) throw new IllegalArgumentException("잘못된 이메일 주소입니다.");

        return sender.sendVerificationCode(email);
    }

    public boolean findIdByEmail(String email) throws Exception {
        Member findMember = memberRepository.findMemberByRecoveryEmail(email).orElse(null);
        if (findMember == null) {
            Thread.sleep(3500);
            return false;
        }

        if (Strings.isNullOrEmpty(findMember.getRecoveryEmail())) {
            Thread.sleep(3500);
            return false;
        }

        MemberRecord memberRecord = findMember.toMemberRecord();
        boolean result = sender.sendRecoveryEmail(memberRecord.recoveryEmail(), memberRecord.email());

        return result;
    }

    public boolean changePassword(ChangeUserPwVo reqVo) {
        if (!reqVo.validParameters()) {
            throw new IllegalArgumentException("잘못된 값을 요청하였습니다.");
        }

        Member member = memberRepository.findByEmail(reqVo.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("No user by email"));

        member.setNewPassword(reqVo.getNewPw());

        memberRepository.save(member);

        return true;
    }

}
