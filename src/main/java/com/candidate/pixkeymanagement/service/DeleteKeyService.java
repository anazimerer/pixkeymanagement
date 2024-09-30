package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.exception.NotFoundException;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.candidate.pixkeymanagement.util.MessageConstant.NOT_FOUND_KEY_PIX;
import static com.candidate.pixkeymanagement.util.MessageConstant.UNEXPECTED_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeleteKeyService {

    private final PixKeyRegisterRepository pixKeyRegisterRepository;

    @Transactional
    public PixKeyResponseDTO process(UUID id) {
        PixKeyRegister pixKeyRegister = findActiveRegisterById(id);
        PixKeyRegister pixKeyRegisterDeleted = deletePixKey(pixKeyRegister);
        return convertEntityToResponse(pixKeyRegisterDeleted);
    }

    private PixKeyRegister findActiveRegisterById(UUID id) {
        Optional<PixKeyRegister> pixKeyRegister = pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(id);

        if (pixKeyRegister.isEmpty()) {
            throw new NotFoundException(NOT_FOUND_KEY_PIX);
        }

        return pixKeyRegister.get();
    }

    private PixKeyRegister deletePixKey(PixKeyRegister pixKeyRegister) {
        try {
            pixKeyRegister.setKeyInactivationDate(LocalDateTime.now());
            return pixKeyRegisterRepository.save(pixKeyRegister);
        } catch (Exception e) {
            log.debug("Persistence delete failed");
            throw new UnexpectedTypeException(UNEXPECTED_ERROR);
        }

    }

    private PixKeyResponseDTO convertEntityToResponse(PixKeyRegister pixKeyRegister) {
        try {
            return PixKeyResponseDTO.builder()
                    .message("Chave Pix deletada com sucesso")
                    .id(pixKeyRegister.getId())
                    .keyType(PixKeyType.fromValue(pixKeyRegister.getKeyType()).orElseThrow())
                    .keyValue(pixKeyRegister.getKeyValue())
                    .accountType(AccountType.fromValue(pixKeyRegister.getAccountType()).orElseThrow())
                    .agencyNumber(pixKeyRegister.getAgencyNumber())
                    .accountNumber(pixKeyRegister.getAccountNumber())
                    .accountHolderFirstName(pixKeyRegister.getAccountHolderFirstName())
                    .accountHolderLastName(Objects.nonNull(pixKeyRegister.getAccountHolderLastName()) ?
                            pixKeyRegister.getAccountHolderLastName() : StringUtils.EMPTY)
                    .keyRegistrationDate(pixKeyRegister.getKeyRegistrationDate())
                    .keyInactivationDate(pixKeyRegister.getKeyInactivationDate())
                    .build();

        } catch (Exception e) {
            log.debug("Convert entity to responseDTO failed. Entity id: {}", pixKeyRegister.getId());
            throw new UnexpectedTypeException("Convert entity to responseDTO failed");
        }
    }
}
