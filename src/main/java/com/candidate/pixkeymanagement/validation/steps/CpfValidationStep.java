package com.candidate.pixkeymanagement.validation.steps;


import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.AbstractValidationStep;
import com.candidate.pixkeymanagement.validation.strategy.RegexOnlyNumber;
import com.candidate.pixkeymanagement.validation.strategy.ValidateRegexStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CPF;
import static com.candidate.pixkeymanagement.util.MessageConstant.VALIDATION_FAILED;


@Service
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class CpfValidationStep extends AbstractValidationStep {

    @Override
    protected Optional<PixKeyContext> validateAndApplyNext(PixKeyContext context) {
        if (!CPF.equals(context.getFields().getKeyType())) {
            return Optional.of(context);
        }

        validateFields(context);
        return Optional.of(context);
    }

    private void validateFields(PixKeyContext context) {
        String key = context.getFields().getKeyValue();
        List<ErrorMessageDTO> errorList = context.getErrorList();

        if (!isValidCPFByMod11(key, errorList)) {
            return;
        }

        hasOnlyNumber(key, errorList);
        validateMaxLength(key, errorList);
    }

    private boolean isValidCPFByMod11(String key, List<ErrorMessageDTO> errorList) {
        CPFValidator cpfValidator = new CPFValidator();
        cpfValidator.initialize(null);

        if (Objects.nonNull(key) && cpfValidator.isValid(key, null)) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CPF.getValue(), "CPF inválido"));
        return false;
    }

    private void validateMaxLength(String cpf, List<ErrorMessageDTO> errorList) {
        cpf = extractCpfNumber(cpf);

        if (Objects.nonNull(cpf) && cpf.length() <= 11) {
            return;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CPF.getValue(), "Campo valorChave excedeu tamanho máximo"));
    }

    private void hasOnlyNumber(String cpf, List<ErrorMessageDTO> errorList) {
        ValidateRegexStrategy regexOnlyNumber = new RegexOnlyNumber();
        cpf = extractCpfNumber(cpf);

        if (Objects.nonNull(cpf) && regexOnlyNumber.validate(cpf)) {
            return;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CPF.getValue(), "Campo do tipo CPF não deve ser preenchido com letras"));
    }

    private String extractCpfNumber(String key) {
        return Objects.nonNull(key) ? key.replaceAll("\\D", "") : null;
    }

}