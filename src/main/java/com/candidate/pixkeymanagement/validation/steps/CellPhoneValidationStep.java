package com.candidate.pixkeymanagement.validation.steps;


import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.AbstractValidationStep;
import com.candidate.pixkeymanagement.validation.strategy.RegexOnlyNumber;
import com.candidate.pixkeymanagement.validation.strategy.ValidateRegexStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CELLPHONE;
import static com.candidate.pixkeymanagement.util.MessageConstant.VALIDATION_FAILED;
import static com.candidate.pixkeymanagement.util.RegexConstant.COUNTRY_CODE_REGEX;
import static com.candidate.pixkeymanagement.util.RegexConstant.TWO_OR_THREE_DIGITS_REGEX;

@Service
@RequiredArgsConstructor
@Order(0)
@Slf4j
public class CellPhoneValidationStep extends AbstractValidationStep {

    private final PixKeyRegisterRepository pixKeyRegisterRepository;

    @Override
    protected Optional<PixKeyContext> validateAndApplyNext(PixKeyContext context) {
        if (!CELLPHONE.equals(context.getFields().getKeyType())) {
            return Optional.of(context);
        }

        validateFields(context);
        return Optional.of(context);
    }

    private void validateFields(PixKeyContext context) {
        List<ErrorMessageDTO> errorList = context.getErrorList();
        String key = context.getFields().getKeyValue();

        if (!checkMaxLengthRule(key, errorList)) {
            return;
        }

        hasValidCountryCode(key, errorList);
        hasOnlyNumberPhoneNumber(key, errorList);
        hasOnlyNumbersDDD(key, errorList);
        hasValidDDD(key, errorList);
    }

    private boolean checkMaxLengthRule(String key, List<ErrorMessageDTO> errorList) {
        if (Objects.nonNull(key) && key.length() <= 15) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Campo valorChave excedeu tamanho máximo"));
        return false;
    }

    private boolean hasValidCountryCode(String key, List<ErrorMessageDTO> errorList) {
        String countryCode = extractCountryCode(key);

        if (Objects.nonNull(countryCode) && countryCode.matches(COUNTRY_CODE_REGEX)) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Código de país inválido"));

        return false;
    }

    private boolean hasValidDDD(String key, List<ErrorMessageDTO> errorList) {
        String ddd = extractDDD(key);

        if ((ddd.matches(TWO_OR_THREE_DIGITS_REGEX))) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "DDD não possui quantidade de dígitos necessárias"));
        return false;
    }

    private boolean hasOnlyNumbersDDD(String key, List<ErrorMessageDTO> errorList) {
        ValidateRegexStrategy regexOnlyNumber = new RegexOnlyNumber();

        String ddd = extractDDD(key);

        if (regexOnlyNumber.validate(ddd)) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "DDD não pode conter letras"));
        return false;
    }

    private boolean hasOnlyNumberPhoneNumber(String key, List<ErrorMessageDTO> errorList) {
        ValidateRegexStrategy regexOnlyNumber = new RegexOnlyNumber();
        String phoneNumber = extractPhoneNumber(key);

        if (Objects.nonNull(phoneNumber) && regexOnlyNumber.validate(phoneNumber)) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Número de telefone não pode conter letras"));
        return false;
    }

    private String extractCountryCode(String key) {
        return Objects.nonNull(key) && key.startsWith("+") && key.length() >= 4 ? key.substring(0, 3) : null;
    }

    private String extractDDD(String phoneNumber) {
        String phoneNumberWithoutCountryCode = phoneNumber.substring(3).trim();
        return phoneNumberWithoutCountryCode.substring(0, phoneNumberWithoutCountryCode.length() - 9).trim();
    }

    private String extractPhoneNumber(String key) {
        return key.length() > 9 ? key.substring(key.length() - 9) : null;
    }

//+55 19 954545467
//    a) Celular:
//    i) Deve validar se valor já existe cadastrado
//    ii) Deve possuir o código pais
//            (1) Deve ser numérico (não aceitar letras)
//            (2) Deve ser de até dois dígitos OK
//            (3) Deve iniciar com o símbolo “+” OK
//    iii) Deve possuir DDD
//            (1) Deve ser numérico (não aceitar letras) ok
//            (2) Deve ser de até três dígitos ok
//    iv) Número com nove dígitos
//            (1) Deve ser numérico (não aceitar letras)
}