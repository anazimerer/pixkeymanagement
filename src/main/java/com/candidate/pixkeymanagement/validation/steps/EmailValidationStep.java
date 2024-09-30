package com.candidate.pixkeymanagement.validation.steps;


import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.AbstractValidationStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.EMAIL;
import static com.candidate.pixkeymanagement.util.MessageConstant.VALIDATION_FAILED;

@Service
@RequiredArgsConstructor
@Order(2)
@Slf4j
public class EmailValidationStep extends AbstractValidationStep {

    @Override
    protected Optional<PixKeyContext> validateAndApplyNext(PixKeyContext context) {
        if (!PixKeyType.EMAIL.equals(context.getFields().getKeyType())) {
            return Optional.of(context);
        }

        validateFields(context);
        return Optional.of(context);
    }

    private void validateFields(PixKeyContext context) {
        List<ErrorMessageDTO> errorList = context.getErrorList();
        String key = context.getFields().getKeyValue();

        if (Objects.isNull(key)) {
            errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, EMAIL.getValue(), "Email inválido"));
            return;
        }

        containsAtSymbol(key, errorList);
        validateMaxLength(key, errorList);
    }

    private void containsAtSymbol(String key, List<ErrorMessageDTO> errorList) {
        if (key.contains("@")) {
            return;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, EMAIL.getValue(), "Email inválido"));
    }

    private void validateMaxLength(String email, List<ErrorMessageDTO> errorList) {
        if (email.length() <= 77) {
            return;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, EMAIL.getValue(), "Campo valorChave excedeu tamanho máximo"));
    }

}

