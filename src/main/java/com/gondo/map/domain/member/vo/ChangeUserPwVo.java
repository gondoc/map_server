package com.gondo.map.domain.member.vo;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserPwVo {
    private String email;
    private String pw;
    private String newPw;

    public boolean validParameters() {
        // origin 값이 없습니다.
        if (Strings.isNullOrEmpty(this.pw)) return false;

        // 새로운 패스워드 값이 없습니다.
        if (Strings.isNullOrEmpty(this.newPw)) return false;

        // 변경되는 값이 일치 할 수 없습니다.
        if (pw.equals(newPw)) return false;

        //6글자 이상 15글자 이내 여야 합니다.
        if (!(5 < newPw.length() && newPw.length() < 16)) return false;

        return true;
    }
}
