package com.gondo.map.domain.common;

import lombok.Getter;

@Getter
public enum Message {
    SUCCESS(200, "SUCCESS"),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증되지 않은 요청입니다."),
    FORBIDDEN(403, "인가되지 않은 요청입니다."),
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    NOT_ALLOWED(405, "허용되지 않은 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),
    ;

    private int code;
    private String message;

    Message(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
