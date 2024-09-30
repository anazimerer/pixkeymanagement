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
class EmailValidationStepTest {

    private final String validEmail = "lorem.ipsum@gmail.com";
    @InjectMocks
    private EmailValidationStep step;
    private PixKeyContext pixKeyContext;

    @BeforeEach
    void setUp() {
        pixKeyContext = getContextFields();
    }

    @Test
    void shouldReturnSuccessWhenEmailIsValid() {
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldReturnSuccessWhenEmailIsAlfaNumeric() {
        pixKeyContext.getFields().setKeyValue("123.ipsum@gmail.com");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorIfEmailDoesNotContainAtSymbol() {
        pixKeyContext.getFields().setKeyValue("lorem.ipsum.gmail.com");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, PixKeyType.EMAIL.getValue(), "Email inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorWhenEmailHasMoreThanSeventySevenCharacters() {
        pixKeyContext.getFields().setKeyValue("lorem.ipsum.dolor.sit.amet.consectetur.adipiscing.elit.sed.do.eiusmod@exemplo.com");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, PixKeyType.EMAIL.getValue(), "Campo valorChave excedeu tamanho máximo");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());

    }

    @Test
    void shouldNotProcessWhenKeyTypeIsDifferentFromEmail() {
        pixKeyContext.getFields().setKeyType(PixKeyType.CNPJ);
        assertDoesNotThrow(() -> step.validateAndApplyNext(pixKeyContext));
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorIfEmailIsNull() {
        pixKeyContext.getFields().setKeyValue(null);
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
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

}