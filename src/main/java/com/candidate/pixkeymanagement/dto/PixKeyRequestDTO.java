package com.candidate.pixkeymanagement.dto;

import com.candidate.pixkeymanagement.enumeration.AccountType;
import com.candidate.pixkeymanagement.enumeration.PixKeyType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static com.candidate.pixkeymanagement.util.MessageConstant.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PixKeyRequestDTO {

    @NotNull(message = FIELD_REQUIRED)
    @JsonProperty("tipoChave")
    private PixKeyType keyType;

    @NotBlank(message = FIELD_REQUIRED)
    @JsonProperty("valorChave")
    @Length(min = 1, max = 70, message = FIELD_MAX_LENGTH)
    private String keyValue;

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

    @NotBlank(message = FIELD_REQUIRED)
    @JsonProperty("nomeCorrentista")
    @Length(min = 1, max = 30, message = FIELD_MAX_LENGTH)
    private String accountHolderFirstName;

    @JsonProperty("sobrenomeCorrentista")
    @Length(max = 45, message = FIELD_MAX_LENGTH)
    private String accountHolderLastName;

}