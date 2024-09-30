package com.candidate.pixkeymanagement.dto;

import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PixKeyResponseDTO {

    @JsonProperty("mensagem")
    private String message;

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("tipoChave")
    private PixKeyType keyType;

    @JsonProperty("valorChave")
    private String keyValue;

    @JsonProperty("tipoConta")
    private AccountType accountType;

    @JsonProperty("numeroAgencia")
    private Integer agencyNumber;

    @JsonProperty("numeroConta")
    private Long accountNumber;

    @JsonProperty("nomeCorrentista")
    private String accountHolderFirstName;

    @JsonProperty("sobrenomeCorrentista")
    private String accountHolderLastName;

    @JsonProperty("dataHoraInclusao")
    private LocalDateTime keyRegistrationDate;

    @JsonProperty("dataHoraInativacao")
    private LocalDateTime keyInactivationDate;
}
