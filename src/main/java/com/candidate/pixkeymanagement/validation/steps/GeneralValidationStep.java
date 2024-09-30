package com.candidate.pixkeymanagement.validation.steps;


import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.AbstractValidationStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CNPJ;
import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CPF;
import static com.candidate.pixkeymanagement.util.MessageConstant.*;

@Service
@RequiredArgsConstructor
@Order(3)
@Slf4j
public class GeneralValidationStep extends AbstractValidationStep {

    private final PixKeyRegisterRepository pixKeyRegisterRepository;

    @Override
    protected Optional<PixKeyContext> validateAndApplyNext(PixKeyContext context) {
        boolean isValid = validate(context);
        if (!isValid) {
            log.debug("Key cellphone Validation failed. Error list: {}", context.getErrorList());
        }

        return Optional.of(context);
    }

    private boolean validate(PixKeyContext context) {
        List<ErrorMessageDTO> errorList = context.getErrorList();
        String key = context.getFields().getKeyValue();

        if (!checkIfKeyIsNonNull(key, errorList)) {
            return false;
        }

        return hasAlreadyRegistered(key, errorList) && hasMoreThanFiveOrTwentyKeysRegisters(context.getFields(), errorList);
    }

    private boolean checkIfKeyIsNonNull(String key, List<ErrorMessageDTO> errorList) {
        if (Objects.nonNull(key)) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(VALIDATION_FAILED, "Falha ao validar campo valorChave"));
        return false;
    }

    private boolean hasAlreadyRegistered(String key, List<ErrorMessageDTO> errorList) {
        if (ObjectUtils.isEmpty(pixKeyRegisterRepository.findByKeyValue(key))) {
            return true;
        }

        errorList.add(new ErrorMessageDTO(KEY_ALREADY_REGISTERED));
        return false;
    }

    private boolean hasMoreThanFiveOrTwentyKeysRegisters(PixKeyRequestDTO pixKeyRequestDTO, List<ErrorMessageDTO> errorList) {
        Integer countRegisters = pixKeyRegisterRepository.countByAgencyNumberAndAccountNumberAndAccountType(pixKeyRequestDTO.getAgencyNumber(),
                pixKeyRequestDTO.getAccountNumber(), pixKeyRequestDTO.getAccountType().getValue());

        if (PixKeyType.CPF.equals(pixKeyRequestDTO.getKeyType()) && countRegisters > 5) {
            errorList.add(new ErrorMessageDTO(EXCEEDED_REGISTERS_FOR_TYPE, CPF.getValue()));
            return false;
        }

        if (PixKeyType.CNPJ.equals(pixKeyRequestDTO.getKeyType()) && countRegisters > 20) {
            errorList.add(new ErrorMessageDTO(EXCEEDED_REGISTERS_FOR_TYPE, CNPJ.getValue()));
            return false;
        }

        return true;
    }
}
