package com.candidate.pixkeymanagement.exception;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class UnprocessableEntityException extends RuntimeException {

    private final HttpStatus status;
    private final transient List<ErrorMessageDTO> errors;

    public UnprocessableEntityException(List<ErrorMessageDTO> errors) {
        this.status = HttpStatus.UNPROCESSABLE_ENTITY;
        this.errors = errors;
    }
}
