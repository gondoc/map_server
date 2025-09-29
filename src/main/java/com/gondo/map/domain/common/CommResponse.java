package com.gondo.map.domain.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.gondo.map.domain.common.Message.*;

@Data
@NoArgsConstructor
public class CommResponse<T> {

    private int code;
    private String message;
    private T data;

    private CommResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommResponse<T> response200(T data) {
        return new CommResponse<>(SUCCESS.getCode(), SUCCESS.getMessage(), data);
    }

    public static <T> CommResponse<T> response400(T data) {
        return new CommResponse<>(BAD_REQUEST.getCode(), BAD_REQUEST.getMessage(), data);
    }

    public static <T> CommResponse<T> response401(T data) {
        return new CommResponse<>(UNAUTHORIZED.getCode(), UNAUTHORIZED.getMessage(), data);
    }

    public static <T> CommResponse<T> response403(T data) {
        return new CommResponse<>(FORBIDDEN.getCode(), FORBIDDEN.getMessage(), data);
    }

    public static <T> CommResponse<T> response405(T data) {
        return new CommResponse<>(NOT_ALLOWED.getCode(), NOT_ALLOWED.getMessage(), data);
    }

    public static <T> CommResponse<T> response405(T data, String msg) {
        return new CommResponse<>(NOT_ALLOWED.getCode(), msg, data);
    }

    public static <T> CommResponse<T> response404(T data) {
        return new CommResponse<>(NOT_FOUND.getCode(), NOT_FOUND.getMessage(), data);
    }

    public static <T> CommResponse<T> response500(T data) {
        return new CommResponse<>(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMessage(), data);
    }

    public static <T> CommResponse setResponseByList(List<T> returnList) {
        CommResponse response = new CommResponse();
        if (returnList != null && !returnList.isEmpty()) {
            response = new CommResponse<>(SUCCESS.getCode(), SUCCESS.getMessage(), returnList);
        } else if (returnList == null) {
            response = new CommResponse<>(BAD_REQUEST.getCode(), BAD_REQUEST.getMessage(), null);
        } else if (returnList.isEmpty()) {
            response = new CommResponse<>(SUCCESS.getCode(), SUCCESS.getMessage(), new ArrayList<>());
        }
        return response;
    }

    public static <T> CommResponse<T> setResponseByData(T data) {
        if (data != null) {
            return response200(data);
        } else {
            return response400(null);
        }
    }

    public static CommResponse<Boolean> setResponseByBoolean(boolean result) {
        return new CommResponse<>(
                result ? SUCCESS.getCode() : BAD_REQUEST.getCode(),
                result ? SUCCESS.getMessage() : BAD_REQUEST.getMessage(),
                result
        );
    }
}

