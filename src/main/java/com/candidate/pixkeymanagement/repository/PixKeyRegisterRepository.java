package com.candidate.pixkeymanagement.repository;


import com.candidate.pixkeymanagement.model.PixKeyRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PixKeyRegisterRepository extends JpaRepository<PixKeyRegister, UUID>, JpaSpecificationExecutor<PixKeyRegister> {

    List<PixKeyRegister> findByKeyValue(String key);

    Optional<PixKeyRegister> findByIdAndKeyInactivationDateIsNull(UUID key);

    Integer countByAgencyNumberAndAccountNumberAndAccountType(Integer agencyNumber, Long accountNumber, String accountType);
}
