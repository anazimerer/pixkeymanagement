package com.candidate.pixkeymanagement.exception;

import lombok.Getter;

@Getter
public class UnexpectedException extends RuntimeException {

    public UnexpectedException(String message) {
        super(message);
    }

}
