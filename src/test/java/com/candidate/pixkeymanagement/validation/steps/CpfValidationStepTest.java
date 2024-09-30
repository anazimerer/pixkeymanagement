package com.candidate.pixkeymanagement.validation.steps;

import com.candidate.pixkeymanagement.dto.ErrorMessageDTO;
import com.candidate.pixkeymanagement.dto.PixKeyRequestDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.candidate.pixkeymanagement.util.MessageConstant.VALIDATION_FAILED;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CpfValidationStepTest {

    private final String cpfWithMask = "949.286.190-99";
    @InjectMocks
    private CpfValidationStep step;
    private PixKeyContext pixKeyContext;

    @BeforeEach
    void setUp() {
        pixKeyContext = getContextFields();
    }

    @Test
    void shouldReturnSuccessWhenCpfIsValid() {
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldReturnSuccessWhenCpfIsValidAndWithoutMask() {
        pixKeyContext.getFields().setKeyValue("94928619099");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorWhenCpfIsInvalidByMod11() {
        pixKeyContext.getFields().setKeyValue("123.123.190-99");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, PixKeyType.CPF.getValue(), "CPF inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }


    @Test
    void shouldThrowErrorIfCpfHasLetters() {
        pixKeyContext.getFields().setKeyValue("a49.286.190-99");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, PixKeyType.CPF.getValue(), "CPF inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorIfCpfHasMoreThanElevenDigits() {
        pixKeyContext.getFields().setKeyValue("987.949.286.190-99");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, PixKeyType.CPF.getValue(), "CPF inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorIfCpfIsNull() {
        pixKeyContext.getFields().setKeyValue(null);
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
    }

    @Test
    void shouldNotProcessWhenKeyTypeIsDifferentFromCpf() {
        pixKeyContext.getFields().setKeyType(PixKeyType.RANDOM_KEY);
        assertDoesNotThrow(() -> step.validateAndApplyNext(pixKeyContext));
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    private PixKeyContext getContextFields() {
        PixKeyRequestDTO pixKeyRequestDTO = PixKeyRequestDTO.builder()
                .keyType(PixKeyType.CPF)
                .keyValue(cpfWithMask)
                .accountType(AccountType.CHECKING)
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem Ipsum")
                .accountHolderLastName("Lorem Ipsum")
                .build();

        return new PixKeyContext(pixKeyRequestDTO);
    }
}