package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.exception.UnprocessableEntityException;
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
import java.util.List;

import static com.candidate.pixkeymanagement.util.MessageConstant.VALIDATION_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterKeyServiceTest {

    @InjectMocks
    private RegisterKeyService registerKeyService;

    @Mock
    private PixKeyRegisterRepository pixKeyRegisterRepository;

    @Mock
    private ValidationStepEngine validationStepEngine;

    private PixKeyContext pixKeyContext;
    private PixKeyRequestDTO pixKeyRequestDTO;
    private PixKeyRegister pixKeyRegister;

    @BeforeEach
    void setUp() {
        pixKeyRequestDTO = getContextFields();
        pixKeyContext = new PixKeyContext(pixKeyRequestDTO);
        pixKeyRegister = getPixKeyRegisterEntity();
    }

    @Test
    void registerKeySuccessfully() {
        when(validationStepEngine.validation(any())).thenReturn(pixKeyContext);
        when(pixKeyRegisterRepository.save(any())).thenReturn(pixKeyRegister);

        PixKeyResponseDTO responseDTO = registerKeyService.process(pixKeyRequestDTO);
        assertEquals(responseDTO.getMessage(), "Chave Pix cadastrada com sucesso");
    }

    @Test
    void shouldThrowExceptionWhenValidationReturnError() {
        pixKeyContext.getFields().setKeyValue(null);
        pixKeyContext.setErrorList(List.of(new ErrorMessageDTO(VALIDATION_FAILED)));

        when(validationStepEngine.validation(any())).thenReturn(pixKeyContext);
        assertThrows(UnprocessableEntityException.class, () -> registerKeyService.process(pixKeyRequestDTO));
        verify(pixKeyRegisterRepository, never()).save(any(PixKeyRegister.class));
    }

    @Test
    void shouldThrowExceptionWhenPersistFailed() {
        when(validationStepEngine.validation(any())).thenReturn(pixKeyContext);
        when(pixKeyRegisterRepository.save(any(PixKeyRegister.class))).thenThrow(new RuntimeException());
        assertThrows(UnexpectedTypeException.class, () -> registerKeyService.process(pixKeyRequestDTO));

        verify(pixKeyRegisterRepository, times(1)).save(any(PixKeyRegister.class));
    }

    private PixKeyRequestDTO getContextFields() {
        return PixKeyRequestDTO.builder()
                .keyType(PixKeyType.CELLPHONE)
                .keyValue("+55011912341234")
                .accountType(AccountType.CHECKING)
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem Ipsum")
                .accountHolderLastName("Lorem Ipsum")
                .build();
    }

    private PixKeyRegister getPixKeyRegisterEntity() {
        return PixKeyRegister.builder()
                .keyType(PixKeyType.CELLPHONE.getValue())
                .keyValue("+55011912341234")
                .accountType(AccountType.CHECKING.getValue())
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem")
                .accountHolderLastName("Lorem Ipsum")
                .keyRegistrationDate(LocalDateTime.now())
                .build();
    }

}