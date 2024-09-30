package com.candidate.pixkeymanagement.dto;

import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

import static com.candidate.pixkeymanagement.util.MessageConstant.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PixKeyUpdateRequestDTO {

    @NotNull(message = FIELD_REQUIRED)
    @JsonProperty("id")
    private UUID id;

    @NotNull(message = FIELD_REQUIRED)
    @JsonProperty("tipoConta")
    private AccountType accountType;

    @NotNull(message = FIELD_REQUIRED)
    @JsonProperty("numeroAgencia")
    @Min(value = 1, message = FIELD_MIN_LENGTH)
    @Max(value = 9999, message = FIELD_MAX_LENGTH)
    private Integer agencyNumber;

    @NotNull(message = FIELD_REQUIRED)
    @JsonProperty("numeroConta")
    @Min(value = 1L, message = FIELD_MIN_LENGTH)
    @Max(value = 99999999L, message = FIELD_MAX_LENGTH)
    private Long accountNumber;

    @NotNull(message = FIELD_REQUIRED)
    @JsonProperty("nomeCorrentista")
    @Length(min = 1, max = 30, message = FIELD_MAX_LENGTH)
    private String accountHolderFirstName;

    @JsonProperty("sobrenomeCorrentista")
    @Length(max = 45, message = FIELD_MAX_LENGTH)
    private String accountHolderLastName;

}