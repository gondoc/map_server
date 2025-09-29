package com.gondo.map.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegRecoveryMailReqDto {
    private String token;
    private String recoveryMail;
}
