package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.dto.PixKeyUpdateRequestDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.exception.NotFoundException;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.orchestrator.ValidationStepEngine;
import jakarta.validation.UnexpectedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateKeyServiceTest {

    @InjectMocks
    private UpdateKeyService updateKeyService;

    @Mock
    private PixKeyRegisterRepository pixKeyRegisterRepository;

    @Mock
    private ValidationStepEngine validationStepEngine;

    private PixKeyUpdateRequestDTO pixKeyUpdateRequestDTO;
    private PixKeyRegister pixKeyRegister;

    @BeforeEach
    void setUp() {
        pixKeyUpdateRequestDTO = getPixKeyUpdateRequestDTO();
        pixKeyRegister = getPixKeyRegisterEntity();
    }

    @Test
    void shouldUpdateKeySuccessfully() {
        PixKeyContext context = new PixKeyContext(getPixKeyRequestDTO());
        context.setTransactionType("PATCH");

        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(any(UUID.class)))
                .thenReturn(Optional.of(pixKeyRegister));
        when(validationStepEngine.validation(any())).thenReturn(context);
        when(pixKeyRegisterRepository.save(any())).thenReturn(pixKeyRegister);

        PixKeyResponseDTO responseDTO = updateKeyService.process(pixKeyUpdateRequestDTO);
        assertNotNull(responseDTO);
        assertEquals("Chave Pix atualizada com sucesso", responseDTO.getMessage());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenPixKeyNotFound() {
        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> updateKeyService.process(pixKeyUpdateRequestDTO));
    }

    @Test
    void shouldThrowUnexpectedTypeExceptionWhenPersistFails() {
        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(any(UUID.class)))
                .thenReturn(Optional.of(pixKeyRegister));
        when(validationStepEngine.validation(any())).thenReturn(new PixKeyContext(getPixKeyRequestDTO()));
        when(pixKeyRegisterRepository.save(any())).thenThrow(new RuntimeException());

        assertThrows(UnexpectedTypeException.class, () -> updateKeyService.process(pixKeyUpdateRequestDTO));
    }

    private PixKeyUpdateRequestDTO getPixKeyUpdateRequestDTO() {
        return PixKeyUpdateRequestDTO.builder()
                .id(UUID.randomUUID())
                .accountType(AccountType.CHECKING)
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem Ipsum")
                .accountHolderLastName("Lorem")
                .build();
    }

    private PixKeyRequestDTO getPixKeyRequestDTO() {
        return PixKeyRequestDTO.builder()
                .keyType(PixKeyType.CELLPHONE)
                .keyValue("+55011912341234")
                .accountType(AccountType.CHECKING)
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem Ipsum")
                .accountHolderLastName("Lorem")
                .build();
    }

    private PixKeyRegister getPixKeyRegisterEntity() {
        return PixKeyRegister.builder()
                .id(UUID.randomUUID())
                .keyType(PixKeyType.CELLPHONE.getValue())
                .keyValue("+55011912341234")
                .accountType(AccountType.CHECKING.getValue())
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem")
                .accountHolderLastName("Lorem")
                .keyRegistrationDate(LocalDateTime.now())
                .build();
    }
}
