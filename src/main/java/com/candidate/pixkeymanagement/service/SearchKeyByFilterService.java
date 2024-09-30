package com.candidate.pixkeymanagement.service;

import com.candidate.pixkeymanagement.dto.PixKeyResponseDTO;
import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.candidate.pixkeymanagement.model.PixKeyRegister;
import com.candidate.pixkeymanagement.repository.PixKeyRegisterRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.candidate.pixkeymanagement.util.MessageConstant.UNEXPECTED_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchKeyByFilterService {

    private final PixKeyRegisterRepository pixKeyRegisterRepository;

    @Transactional
    public List<PixKeyResponseDTO> process(String keyType, String agencyNumber, String accountNumber, String accountHolderFirstName) {
        log.debug("Start GET/ by filters");

        List<PixKeyRegister> pixKeyRegisterList = findRegisterByFilters(keyType, agencyNumber, accountNumber, accountHolderFirstName);
        List<PixKeyResponseDTO> pixKeyResponseListDTO = convertEntityToResponseListDTO(pixKeyRegisterList);

        log.debug("Finish GET/ by filters. Response: {}", pixKeyResponseListDTO);
        return pixKeyResponseListDTO;
    }

    private List<PixKeyResponseDTO> convertEntityToResponseListDTO(List<PixKeyRegister> pixKeyRegisterList) {
        try {
            log.debug("Converting entityList to responseDTOList");
            return pixKeyRegisterList.stream().map(this::convertEntityToResponseDTO).toList();
        } catch (Exception e) {
            log.error("Convert entity to responseDTO failed");
            throw new UnexpectedTypeException(UNEXPECTED_ERROR);
        }
    }

    private PixKeyResponseDTO convertEntityToResponseDTO(PixKeyRegister pixKeyRegisterUpdated) {
        try {
            log.debug("Converting pixKeyRegister entity to responseDTO");
            return PixKeyResponseDTO.builder()
                    .id(pixKeyRegisterUpdated.getId())
                    .keyType(PixKeyType.fromValue(pixKeyRegisterUpdated.getKeyType()).orElseThrow())
                    .keyValue(pixKeyRegisterUpdated.getKeyValue())
                    .accountType(AccountType.fromValue(pixKeyRegisterUpdated.getAccountType()).orElseThrow())
                    .agencyNumber(pixKeyRegisterUpdated.getAgencyNumber())
                    .accountNumber(pixKeyRegisterUpdated.getAccountNumber())
                    .accountHolderFirstName(pixKeyRegisterUpdated.getAccountHolderFirstName())
                    .accountHolderLastName(Objects.nonNull(pixKeyRegisterUpdated.getAccountHolderLastName()) ?
                            pixKeyRegisterUpdated.getAccountHolderLastName() : StringUtils.EMPTY)
                    .keyRegistrationDate(pixKeyRegisterUpdated.getKeyRegistrationDate())
                    .keyInactivationDate(pixKeyRegisterUpdated.getKeyInactivationDate())
                    .build();

        } catch (Exception e) {
            log.debug("Convert entity to responseDTO failed. Entity id: {}", pixKeyRegisterUpdated.getId());
            throw new UnexpectedTypeException(UNEXPECTED_ERROR);
        }
    }

    public List<PixKeyRegister> findRegisterByFilters(String keyType, String agencyNumber, String accountNumber, String accountHolderFirstName) {
        log.debug("Start findRegisterByFilters");
        return pixKeyRegisterRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(keyType)) {
                log.debug("Searching for key type {}", keyType);
                predicates.add(criteriaBuilder.equal(root.get("keyType"), keyType));
            }
            if (Objects.nonNull(agencyNumber)) {
                log.debug("Searching for agency number {}", agencyNumber);
                predicates.add(criteriaBuilder.equal(root.get("agencyNumber"), agencyNumber));
            }
            if (Objects.nonNull(accountNumber)) {
                log.debug("Searching for accountNumber {}", accountNumber);
                predicates.add(criteriaBuilder.equal(root.get("accountNumber"), accountNumber));
            }
            if (Objects.nonNull(accountHolderFirstName)) {
                log.debug("Searching for and accountHolderFirstName {}", accountHolderFirstName);
                predicates.add(criteriaBuilder.equal(root.get("accountHolderFirstName"), accountHolderFirstName));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
