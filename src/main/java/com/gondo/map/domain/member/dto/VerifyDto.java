package com.gondo.map.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyDto {
    private String email;
    private String code;
}
