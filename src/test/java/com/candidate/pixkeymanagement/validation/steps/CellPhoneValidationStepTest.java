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

import static com.candidate.pixkeymanagement.enumeration.PixKeyType.CELLPHONE;
import static com.candidate.pixkeymanagement.util.MessageConstant.VALIDATION_FAILED;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CellPhoneValidationStepTest {

    String phoneNumber = "+55019912341234";
    @InjectMocks
    private CellPhoneValidationStep step;
    private PixKeyContext pixKeyContext;

    @BeforeEach
    void setUp() {
        pixKeyContext = getContextFields();
    }

    @Test
    void shouldReturnSuccessMessageWhenPhoneNumberIsValid() {
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorWhenPhoneNumberHasLetters() {
        pixKeyContext.getFields().setKeyValue("+551991234123a");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Número de telefone não pode conter letras");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorIfDDDHasLetters() {
        pixKeyContext.getFields().setKeyValue("+55a9912341239");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "DDD não pode conter letras");

        assertTrue(context.isPresent());
        assertEquals(2, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldReturnSuccessMessageWhenDDDHasTwoDigits() {
        pixKeyContext.getFields().setKeyValue("+5511912341234");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldReturnSuccessMessageWhenDDDHasTreeDigits() {
        pixKeyContext.getFields().setKeyValue("+55011912341234");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorWhenDDDHasOneDigits() {
        pixKeyContext.getFields().setKeyValue("+550912341234");
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "DDD não possui quantidade de dígitos necessárias");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorWhenKeyValueBrokeMaxLengthRule() {
        pixKeyContext.getFields().setKeyValue("+19123456789123455677");

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Campo valorChave excedeu tamanho máximo");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());

    }

    @Test
    void shouldReturnSuccessWhenCountryCodeStartsWithPlusAndTwoDigits() {
        pixKeyContext.getFields().setKeyValue("+5519123456789");

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorWhenCountryCodeDontStartsWithPlusAndTwoDigits() {
        pixKeyContext.getFields().setKeyValue("55011912345678");

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Código de país inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorIfCountryCodeHasLetters() {
        pixKeyContext.getFields().setKeyValue("+a519123456789");

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Código de país inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldThrowErrorIfCountryCodeNotStartsWithPlus() {
        pixKeyContext.getFields().setKeyValue("55011912341234");

        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        ErrorMessageDTO errorMessageDTO = new ErrorMessageDTO(VALIDATION_FAILED, CELLPHONE.getValue(), "Código de país inválido");

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
        assertEquals(errorMessageDTO.getMessage(), context.get().getErrorList().get(0).getMessage());
    }

    @Test
    void shouldNotProcessWhenKeyTypeIsDifferentFromCellphone() {
        pixKeyContext.getFields().setKeyType(PixKeyType.CNPJ);
        assertDoesNotThrow(() -> step.validateAndApplyNext(pixKeyContext));
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);
        assertTrue(context.isPresent());
        assertEquals(0, context.get().getErrorList().size());
    }

    @Test
    void shouldThrowErrorIfCellphoneIsNull() {
        pixKeyContext.getFields().setKeyValue(null);
        Optional<PixKeyContext> context = step.validateAndApplyNext(pixKeyContext);

        assertTrue(context.isPresent());
        assertEquals(1, context.get().getErrorList().size());
    }

    private PixKeyContext getContextFields() {
        PixKeyRequestDTO pixKeyRequestDTO = PixKeyRequestDTO.builder()
                .keyType(PixKeyType.CELLPHONE)
                .keyValue(phoneNumber)
                .accountType(AccountType.CHECKING)
                .agencyNumber(1234)
                .accountNumber(56789012345L)
                .accountHolderFirstName("Lorem Ipsum")
                .accountHolderLastName("Lorem Ipsum")
                .build();

        return new PixKeyContext(pixKeyRequestDTO);
    }
}

