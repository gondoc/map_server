package com.gondo.map.application.api;

import com.gondo.map.domain.common.CommResponse;
import com.gondo.map.domain.member.dto.ChangeNickReqDto;
import com.gondo.map.domain.member.record.MemberRecord;
import com.gondo.map.domain.member.record.VerifyRecord;
import com.gondo.map.domain.member.service.MemberService;
import com.gondo.map.domain.member.service.RecoveryEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/user")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final RecoveryEmailService recoveryEmailService;

    @PostMapping("/signUp")
    public CommResponse<?> signUp(@RequestBody MemberRecord memberRecord) {
        try {
            return CommResponse.response200(memberService.signUp(memberRecord));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @GetMapping("/checkId/{id}")
    public CommResponse<?> idCheck(@PathVariable(value = "id") String id) {
        try {
            return CommResponse.response200(memberService.idCheck(id));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @GetMapping("/checkNick/{nick}")
    public CommResponse<?> nickCheck(@PathVariable(value = "nick") String nick) {
        try {
            return CommResponse.response200(memberService.nickCheck(nick));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/reqCd")
    public CommResponse<?> sendVerifyCodeToEmail(@RequestBody Map<String, String> reqMap) {
        try {
            return CommResponse.response200(memberService.sendVerifyCode(reqMap.get("email")));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/verifyCd")
    public CommResponse<?> validVerifyCode(@RequestBody VerifyRecord verifyRecord) {
        try {
            return CommResponse.response200(memberService.validVerifyCode(verifyRecord));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @GetMapping("/nick")
    public CommResponse<?> sendRandomNick() {
        try {
            return CommResponse.response200(memberService.sendRandomNick());
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PatchMapping("/nick")
    public CommResponse<?> changeNick(@RequestBody ChangeNickReqDto changeNickReqDto) {
        try {
            return CommResponse.response200(memberService.changeNick(changeNickReqDto));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/findId")
    public CommResponse<?> findId(@RequestBody Map<String, String> reqMap) {
        try {
            return CommResponse.response200(memberService.findIdByEmail(reqMap.get("email")));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/recovery-mail")
    public CommResponse<?> recoveryMail(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String recoveryEmail = body.get("rcvryEmail");
            return CommResponse.response200(recoveryEmailService.regRecoveryMail(email, recoveryEmail));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

    @PostMapping("/verify-mail")
    public CommResponse<?> verifyMail(@RequestBody Map<String, String> body) {
        try {
            return CommResponse.response200(recoveryEmailService.verifyMail(body.get("token")));
        } catch (Exception e) {
            return CommResponse.response400(e.getMessage());
        }
    }

}
