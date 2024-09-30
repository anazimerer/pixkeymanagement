package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.exception.NotFoundException;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
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
class SearchKeyByIdServiceTest {

    @InjectMocks
    private SearchKeyByIdService searchKeyByIdService;

    @Mock
    private PixKeyRegisterRepository pixKeyRegisterRepository;

    private UUID pixKeyId;
    private PixKeyRegister pixKeyRegister;

    @BeforeEach
    void setUp() {
        pixKeyId = UUID.randomUUID();
        pixKeyRegister = getPixKeyRegisterEntity();
    }

    @Test
    void shouldReturnPixKeyResponseDTOWhenKeyFound() {
        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(any(UUID.class)))
                .thenReturn(Optional.of(pixKeyRegister));

        PixKeyResponseDTO responseDTO = searchKeyByIdService.process(pixKeyId);
        assertNotNull(responseDTO);
        assertEquals(pixKeyRegister.getId(), responseDTO.getId());
        assertEquals(PixKeyType.fromValue(pixKeyRegister.getKeyType()).orElseThrow(), responseDTO.getKeyType());
        assertEquals(pixKeyRegister.getKeyValue(), responseDTO.getKeyValue());
        assertEquals(AccountType.fromValue(pixKeyRegister.getAccountType()).orElseThrow(), responseDTO.getAccountType());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenPixKeyNotFound() {
        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> searchKeyByIdService.process(pixKeyId));
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
