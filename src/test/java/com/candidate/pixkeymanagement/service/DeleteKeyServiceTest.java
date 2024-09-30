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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteKeyServiceTest {

    @InjectMocks
    private DeleteKeyService deleteKeyService;

    @Mock
    private PixKeyRegisterRepository pixKeyRegisterRepository;

    private PixKeyRegister pixKeyRegister;

    private UUID pixKeyId;

    @BeforeEach
    void setUp() {
        pixKeyRegister = getPixKeyRegisterEntity();
        pixKeyId = UUID.randomUUID();
    }

    @Test
    void shouldDeleteKeySuccessfully() {
        pixKeyRegister.setKeyInactivationDate(LocalDateTime.now().withNano(0));

        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(pixKeyId))
                .thenReturn(Optional.of(pixKeyRegister));
        when(pixKeyRegisterRepository.save(any())).thenReturn(pixKeyRegister);

        PixKeyResponseDTO responseDTO = deleteKeyService.process(pixKeyId);
        assertEquals(LocalDateTime.now().withNano(0), responseDTO.getKeyInactivationDate().withNano(0));
        verify(pixKeyRegisterRepository, times(1)).save(any());
        verify(pixKeyRegisterRepository, times(1)).findByIdAndKeyInactivationDateIsNull(any());
    }

    @Test
    void shouldThrowNotFoundKey() {
        when(pixKeyRegisterRepository.findByIdAndKeyInactivationDateIsNull(any(UUID.class)))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> deleteKeyService.process(pixKeyId));
        verify(pixKeyRegisterRepository, times(1)).findByIdAndKeyInactivationDateIsNull(any());
        verify(pixKeyRegisterRepository, never()).save(any());
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