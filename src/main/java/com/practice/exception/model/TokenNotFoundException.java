package com.practice.exception.model;

import com.practice.exception.message.ExceptionMessage;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException(String error) {
        super(error);
    }

    public TokenNotFoundException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
