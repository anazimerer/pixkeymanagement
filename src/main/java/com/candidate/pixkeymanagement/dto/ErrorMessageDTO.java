package com.candidate.pixkeymanagement.dto;

import com.candidate.pixkeymanagement.configuration.ApplicationConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorMessageDTO {
    private String message;

    public ErrorMessageDTO(String code, Object... args) {
        this.message = new ApplicationConfig().getMessageSource(code, args);
    }
}
