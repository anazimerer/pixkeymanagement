package com.candidate.pixkeymanagement.validation;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PixKeyContext {

    private PixKeyRequestDTO fields;
    private List<ErrorMessageDTO> errorList;
    private String transactionType;

    public PixKeyContext(PixKeyRequestDTO fields) {
        this.fields = fields;
        this.errorList = new ArrayList<>();
    }
}
