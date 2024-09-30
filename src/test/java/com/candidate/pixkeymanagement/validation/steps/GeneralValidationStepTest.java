package com.candidate.pixkeymanagement.validation.steps;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CNPJ;
import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CPF;
import static com.candidate.pixkeymanagement.util.MessageConstant.EXCEEDED_REGISTERS_FOR_TYPE;
import static com.candidate.pixkeymanagement.util.MessageConstant.KEY_ALREADY_REGISTERED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralValidationStepTest {

    private final String validEmail = "lorem.ipsum@gmail.com";
    @InjectMocks
    private GeneralValidationStep step;
    @Mock
    private PixKeyRegisterRepository pixKeyRegisterRepository;
    private PixKeyContext pixKeyContext;
    private PixKeyRegister pixKeyRegister;

    @BeforeEach
    void setUp() {
        pixKeyContext = getContextFields();
        pixKeyContext.setTransactionType("POST");
        pixKeyRegister = getPixKeyRegisterEntity();
    }

    @Test
    void shouldReturnSuccessIfValueWasNotRegistered() {
        when(pixKeyRegisterRepository.findByKeyValue(pixKeyContext.getFields().getKeyValue())).thenReturn(List.of());

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
        verify(pixKeyRegisterRepository, times(1)).findByKeyValue(anyString());
    }

    @Test
    void shouldThrowErrorIfValueAlreadyRegistered() {
        when(pixKeyRegisterRepository.findByKeyValue(pixKeyContext.getFields().getKeyValue())).thenReturn(List.of(pixKeyRegister));

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(KEY_ALREADY_REGISTERED);

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
        verify(pixKeyRegisterRepository, times(1)).findByKeyValue(anyString());
    }

    @Test
    void shouldThrowErrorIfKeyValueIsNull() {
        pixKeyContext.getFields().setKeyValue(null);
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
    }


    @Test
    void shouldThrowErrorIfMoreThan5KeysAlreadyRegisteredByCPF() {
        pixKeyContext.getFields().setKeyValue("949.286.190-99");
        pixKeyContext.getFields().setKeyType(PixKeyType.CPF);

        when(pixKeyRegisterRepository.countByAgencyNumberAndAccountNumber(pixKeyContext.getFields().getAgencyNumber(),
                pixKeyContext.getFields().getAccountNumber()))
                .thenReturn(6);

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(EXCEEDED_REGISTERS_FOR_TYPE, CPF.getValue());

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
        verify(pixKeyRegisterRepository, times(1)).findByKeyValue(anyString());
        verify(pixKeyRegisterRepository, times(1)).countByAgencyNumberAndAccountNumber(anyInt(), anyLong());
    }

    @Test
    void shouldThrowErrorIfMoreThan20KeysAlreadyRegisteredByCNPJ() {
        pixKeyContext.getFields().setKeyValue("949.286.190-99");
        pixKeyContext.getFields().setKeyType(CNPJ);

        when(pixKeyRegisterRepository.countByAgencyNumberAndAccountNumber(pixKeyContext.getFields().getAgencyNumber(),
                pixKeyContext.getFields().getAccountNumber()))
                .thenReturn(21);

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(EXCEEDED_REGISTERS_FOR_TYPE, CNPJ.getValue());

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
        verify(pixKeyRegisterRepository, times(1)).findByKeyValue(anyString());
        verify(pixKeyRegisterRepository, times(1)).countByAgencyNumberAndAccountNumber(anyInt(), anyLong());
    }


    private PixKeyContext getContextFields() {
        PixKeyRequestDTO pixKeyRequestDTO = PixKeyRequestDTO.builder()
                .keyType(PixKeyType.EMAIL)
                .keyValue(validEmail)
                .accountType(AccountType.CHECKING)
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem Ipsum")
                .accountHolderLastName("Lorem Ipsum")
                .build();

        return new PixKeyContext(pixKeyRequestDTO);
    }

    private PixKeyRegister getPixKeyRegisterEntity() {
        return PixKeyRegister.builder()
                .keyType(PixKeyType.EMAIL.getValue())
                .keyValue(validEmail)
                .accountType(AccountType.CHECKING.getValue())
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem")
                .accountHolderLastName("Lorem Ipsum")
                .keyRegistrationDate(LocalDateTime.now())
                .build();
    }
}