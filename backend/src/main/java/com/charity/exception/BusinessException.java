package com.charity.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 50000;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
