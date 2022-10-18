package com.practice.exception.model;

import com.practice.exception.message.ExceptionMessage;

public class UserAuthException extends RuntimeException {
    public UserAuthException(String error) {
        super(error);
    }

    public UserAuthException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.message());
    }
}
