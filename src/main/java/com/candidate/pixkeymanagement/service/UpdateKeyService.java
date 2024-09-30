package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.dto.PixKeyUpdateRequestDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.exception.NotFoundException;
import com.candidate.pixkeymanagement.exception.UnprocessableEntityException;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.ValidationStepEngine;
import jakarta.transaction.Transactional;
import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.RANDOM_KEY;
import static com.candidate.pixkeymanagement.util.MessageConstant.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UpdateKeyService {

    private final PixKeyRegisterRepository pixKeyRegisterRepository;
    private final ValidationStepEngine validationStepEngine;

    @Transactional
    public PixKeyResponseDTO process(PixKeyUpdateRequestDTO pixKeyUpdateRequestDTO) {
        log.debug("Start pix key update. Request: {}", pixKeyUpdateRequestDTO);

        PixKeyRegister pixKeyRegister = findActiveRegisterById(pixKeyUpdateRequestDTO.getId());
        checkIfIsAvailableUpdate(pixKeyRegister);
        PixKeyRequestDTO pixKeyMergedRequestDTO = mergeEntityAndUpdateRequestDTO(pixKeyUpdateRequestDTO, pixKeyRegister);
        validateValuesToUpdate(pixKeyMergedRequestDTO);
        PixKeyRegister pixKeyRegisterUpdated = persistUpdate(pixKeyUpdateRequestDTO, pixKeyRegister);

        PixKeyResponseDTO pixKeyResponseDTO = convertEntityToResponseDTO(pixKeyRegisterUpdated);

        log.debug("Finish pix key update. Response: {}", pixKeyResponseDTO);
        return pixKeyResponseDTO;
    }

    private void validateValuesToUpdate(PixKeyRequestDTO pixKeyMergedRequestDTO) {
        log.debug("Started validation to update fields. Pix key type: {}", pixKeyMergedRequestDTO.getKeyType().getValue());

        PixKeyContext context = new PixKeyContext(pixKeyMergedRequestDTO);
        context.setTransactionType("PATCH");
        validationStepEngine.validation(context);

        if (ObjectUtils.isNotEmpty(context.getErrorList())) {
            log.error("Validation errors: {}", context.getErrorList());
            throw new UnprocessableEntityException(context.getErrorList());
        }
    }

    private PixKeyRegister persistUpdate(PixKeyUpdateRequestDTO pixKeyUpdateRequestDTO, PixKeyRegister pixKeyRegister) {
        try {
            log.debug("Building pixKeyRegister entity to update");
            pixKeyRegister.setKeyRegistrationDate(LocalDateTime.now());
            pixKeyRegister.setAgencyNumber(pixKeyUpdateRequestDTO.getAgencyNumber());
            pixKeyRegister.setAccountNumber(pixKeyUpdateRequestDTO.getAccountNumber());
            pixKeyRegister.setAccountHolderFirstName(pixKeyUpdateRequestDTO.getAccountHolderFirstName());

            if (Objects.nonNull(pixKeyUpdateRequestDTO.getAccountHolderLastName())) {
                pixKeyRegister.setAccountHolderLastName(pixKeyUpdateRequestDTO.getAccountHolderLastName());
            }

            log.debug("Updating pixKeyRegister in database");
            return pixKeyRegisterRepository.save(pixKeyRegister);
        } catch (Exception e) {
            log.debug("Persistence to update register failed. Entity id: {}", pixKeyRegister.getId());
            throw new UnexpectedTypeException(UNEXPECTED_ERROR);
        }
    }

    private PixKeyRegister findActiveRegisterById(UUID id) {
        log.debug("Searching for active register by id: {}", id);
        Optional<PixKeyRegister> pixKeyRegister = pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(id);

        if (pixKeyRegister.isEmpty()) {
            log.debug("Not found register by id: {}", id);
            throw new NotFoundException(NOT_FOUND_KEY_PIX);
        }

        return pixKeyRegister.get();
    }

    private void checkIfIsAvailableUpdate(PixKeyRegister pixKeyRegister) {
        log.debug("Checking if keyType is available for updating");
        if (RANDOM_KEY.getValue().equals(pixKeyRegister.getKeyType())) {
            log.error("KeyType RANDOM_KEY is not available for updating");
            throw new UnexpectedTypeException(NOT_UPDATED_RANDOM_KEY);
        }
    }

    private PixKeyRequestDTO mergeEntityAndUpdateRequestDTO(PixKeyUpdateRequestDTO pixKeyUpdateRequestDTO, PixKeyRegister pixKeyRegister) {
        try {
            log.debug("Merging pixKeyRegister by database and new fields to update by pixKeyUpdateRequestDTO");
            log.debug("Adapting request to chain of validation");
            return PixKeyRequestDTO.builder()
                    .keyType(PixKeyType.fromValue(pixKeyRegister.getKeyType()).orElseThrow())
                    .keyValue(pixKeyRegister.getKeyValue())
                    .accountType(AccountType.fromValue(pixKeyRegister.getAccountType()).orElseThrow())
                    .agencyNumber(pixKeyUpdateRequestDTO.getAgencyNumber())
                    .accountNumber(pixKeyUpdateRequestDTO.getAccountNumber())
                    .accountHolderFirstName(pixKeyUpdateRequestDTO.getAccountHolderFirstName())
                    .accountHolderLastName(Objects.nonNull(pixKeyUpdateRequestDTO.getAccountHolderLastName()) ?
                            pixKeyUpdateRequestDTO.getAccountHolderLastName() : StringUtils.EMPTY)
                    .build();
        } catch (Exception e) {
            log.debug("Convert request to update register failed. Entity id: {}", pixKeyRegister.getId());
            throw new UnexpectedTypeException("Update converting failed");
        }
    }

    private PixKeyResponseDTO convertEntityToResponseDTO(PixKeyRegister pixKeyRegisterUpdated) {
        try {
            log.debug("Converting pixKeyRegister entity to responseDTO");
            return PixKeyResponseDTO.builder()
                    .message("Chave Pix atualizada com sucesso")
                    .id(pixKeyRegisterUpdated.getId())
                    .keyType(PixKeyType.fromValue(pixKeyRegisterUpdated.getKeyType()).orElseThrow())
                    .keyValue(pixKeyRegisterUpdated.getKeyValue())
                    .accountType(AccountType.fromValue(pixKeyRegisterUpdated.getAccountType()).orElseThrow())
                    .agencyNumber(pixKeyRegisterUpdated.getAgencyNumber())
                    .accountNumber(pixKeyRegisterUpdated.getAccountNumber())
                    .accountHolderFirstName(pixKeyRegisterUpdated.getAccountHolderFirstName())
                    .accountHolderLastName(Objects.nonNull(pixKeyRegisterUpdated.getAccountHolderLastName()) ?
                            pixKeyRegisterUpdated.getAccountHolderLastName() : StringUtils.EMPTY)
                    .keyRegistrationDate(pixKeyRegisterUpdated.getKeyRegistrationDate())
                    .build();

        } catch (Exception e) {
            log.debug("Convert entity to responseDTO failed. Entity id: {}", pixKeyRegisterUpdated.getId());
            throw new UnexpectedTypeException("Convert entity to responseDTO failed");
        }
    }
}
