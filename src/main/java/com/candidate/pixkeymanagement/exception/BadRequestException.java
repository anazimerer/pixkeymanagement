package com.candidate.pixkeymanagement.exception;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final transient ErrorMessageDTO error;

    public BadRequestException(ErrorMessageDTO error) {
        this.error = error;
    }
}
