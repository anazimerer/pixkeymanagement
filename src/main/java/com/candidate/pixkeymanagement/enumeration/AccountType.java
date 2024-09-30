package com.candidate.pixkeymanagement.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;

@AllArgsConstructor
public enum AccountType {

    CHECKING("CORRENTE"),
    SAVINGS("POUPANCA");

    private final String value;

    public static Optional<AccountType> fromValue(String value) {
        return Stream.of(values())
                .filter(type -> type.getValue().equals(value))
                .findFirst();
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

}
