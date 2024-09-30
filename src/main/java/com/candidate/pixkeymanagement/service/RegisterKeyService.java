package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.exception.UnexpectedException;
import com.candidate.pixkeymanagement.exception.UnprocessableEntityException;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.ValidationStepEngine;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.candidate.pixkeymanagement.util.MessageConstant.UNEXPECTED_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterKeyService {

    private final PixKeyRegisterRepository pixKeyRegisterRepository;
    private final ValidationStepEngine validationStepEngine;

    @Transactional
    public PixKeyResponseDTO process(PixKeyRequestDTO pixKeyRequestDTO) {
        log.debug("Started pix key register. Request: {}", pixKeyRequestDTO);

        validate(pixKeyRequestDTO);
        PixKeyRegister pixKeyRegister = persist(pixKeyRequestDTO);
        PixKeyResponseDTO responseDTO = convertEntityToResponse(pixKeyRegister);

        log.debug("Finished pix key register. Response: {}", responseDTO);
        return responseDTO;
    }

    private void validate(PixKeyRequestDTO pixKeyRequestDTO) {
        log.debug("Started validation. Pix key type: {}", pixKeyRequestDTO.getKeyType().getValue());
        PixKeyContext context = validationStepEngine.validation(new PixKeyContext(pixKeyRequestDTO));

        if (ObjectUtils.isNotEmpty(context.getErrorList())) {
            log.error("Validation errors: {}", context.getErrorList());
            throw new UnprocessableEntityException(context.getErrorList());
        }
    }

    private PixKeyRegister persist(PixKeyRequestDTO pixKeyRequestDTO) {
        try {
            log.debug("Building pixKeyRegister entity");
            PixKeyRegister pixKeyRegister = PixKeyRegister.builder()
                    .keyType(pixKeyRequestDTO.getKeyType().getValue())
                    .keyValue(pixKeyRequestDTO.getKeyValue())
                    .accountType(pixKeyRequestDTO.getAccountType().getValue())
                    .agencyNumber(pixKeyRequestDTO.getAgencyNumber())
                    .accountNumber(pixKeyRequestDTO.getAccountNumber())
                    .accountHolderFirstName(pixKeyRequestDTO.getAccountHolderFirstName())
                    .accountHolderLastName(pixKeyRequestDTO.getAccountHolderLastName())
                    .keyRegistrationDate(LocalDateTime.now())
                    .build();

            log.debug("Saving pixKeyRegister entity");
            return pixKeyRegisterRepository.save(pixKeyRegister);
        } catch (Exception e) {
            log.error("Persistence pixKeyRegister entity failed");
            throw new UnexpectedException(UNEXPECTED_ERROR);
        }

    }

    private PixKeyResponseDTO convertEntityToResponse(PixKeyRegister pixKeyRegister) {
        log.debug("Converting pixKeyRegister entity to responseDTO");
        return PixKeyResponseDTO.builder().message("Chave Pix cadastrada com sucesso").id(pixKeyRegister.getId()).build();
    }
}
