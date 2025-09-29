package com.gondo.map.domain.common;

import lombok.Getter;

@Getter
public class CommException extends RuntimeException {
    private int code;

    public CommException() {
        super();
    }

    public CommException(int code, String message) {
        super(message);
        this.code = code;
    }
}
