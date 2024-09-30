package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class SearchKeyByFilterServiceTest {

    @InjectMocks
    private SearchKeyByFilterService searchKeyByFilterService;

    @Mock
    private PixKeyRegisterRepository pixKeyRegisterRepository;

    @Mock
    private Root<PixKeyRegister> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder criteriaBuilder;

    @Mock
    private Predicate predicate;

    private PixKeyRegister pixKeyRegister;

    @BeforeEach
    void setUp() {
        pixKeyRegister = getPixKeyRegisterEntity();
    }

    @Test
    void shouldProcessGetSuccessfully() {
        List<PixKeyRegister> pixKeyRegisters = new ArrayList<>();
        ;
        pixKeyRegisters.add(pixKeyRegister);
        when(pixKeyRegisterRepository.findAll(any(Specification.class))).thenReturn(pixKeyRegisters);

        assertDoesNotThrow(() -> searchKeyByFilterService.process("EMAIL", null, null, null));
    }

    @Test
    void shouldFindByCombinedFiltersSuccessfully() {
        when(pixKeyRegisterRepository.findAll(any(Specification.class))).thenReturn(List.of(pixKeyRegister));
        searchKeyByFilterService.findRegisterByFilters("EMAIL", "1234", "12345678", "Lorem");

        ArgumentCaptor<Specification<PixKeyRegister>> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(pixKeyRegisterRepository, times(1)).findAll(specificationCaptor.capture());

        when(criteriaBuilder.equal(any(), eq("EMAIL"))).thenReturn(predicate);
        specificationCaptor.getValue().toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).equal(root.get("keyType"), "EMAIL");
        verify(criteriaBuilder).equal(root.get("agencyNumber"), "1234");
        verify(criteriaBuilder).equal(root.get("accountNumber"), "12345678");
        verify(criteriaBuilder).equal(root.get("accountHolderFirstName"), "Lorem");
    }

    @Test
    void shouldFindByOneFilterSuccessfully() {
        when(pixKeyRegisterRepository.findAll(any(Specification.class))).thenReturn(List.of(pixKeyRegister));
        searchKeyByFilterService.findRegisterByFilters("EMAIL", null, null, null);

        ArgumentCaptor<Specification<PixKeyRegister>> specificationCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(pixKeyRegisterRepository, times(1)).findAll(specificationCaptor.capture());

        when(criteriaBuilder.equal(any(), eq("EMAIL"))).thenReturn(predicate);
        specificationCaptor.getValue().toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder).equal(root.get("keyType"), "EMAIL");
    }

    private PixKeyRegister getPixKeyRegisterEntity() {
        return PixKeyRegister.builder()
                .id(UUID.randomUUID())
                .keyType(PixKeyType.EMAIL.getValue())
                .keyValue("lorem.lorem@email.vom")
                .accountType(AccountType.CHECKING.getValue())
                .agencyNumber(1234)
                .accountNumber(12345678L)
                .accountHolderFirstName("Lorem")
                .accountHolderLastName("Lorem")
                .keyRegistrationDate(LocalDateTime.now())
                .build();
    }
}