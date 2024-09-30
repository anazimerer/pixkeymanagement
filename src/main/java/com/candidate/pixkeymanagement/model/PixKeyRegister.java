package com.candidate.pixkeymanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pix_key_register")
public class PixKeyRegister {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "key_type", length = 9, nullable = false)
    private String keyType;

    @NotNull
    @Column(name = "key_value", length = 77, nullable = false)
    private String keyValue;

    @NotNull
    @Column(name = "account_type", length = 10, nullable = false)
    private String accountType;

    @NotNull
    @Column(name = "agency_number", nullable = false)
    private Integer agencyNumber;

    @NotNull
    @Column(name = "account_number", nullable = false)
    private Long accountNumber;

    @NotNull
    @Column(name = "account_holder_first_name", length = 30, nullable = false)
    private String accountHolderFirstName;

    @Column(name = "account_holder_last_name", length = 45)
    private String accountHolderLastName;

    @NotNull
    @Column(name = "key_registration_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime keyRegistrationDate;

    @Column(name = "key_inactivation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime keyInactivationDate;

}