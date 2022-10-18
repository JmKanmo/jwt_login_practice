package com.practice.exception.model;

import com.practice.exception.message.ExceptionMessage;

public class TokenCheckFailException extends RuntimeException {
    public TokenCheckFailException(String error) {
        super(error);
    }

    public TokenCheckFailException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
