package com.candidate.pixkeymanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class MessageConstant {

    public static final String NOT_FOUND_KEY_PIX = "404.001";

    public static final String FIELD_REQUIRED = "400.001";
    public static final String FIELD_INVALID = "400.002";
    public static final String FIELD_MAX_LENGTH = "400.003";
    public static final String FIELD_MIN_LENGTH = "400.004";

    public static final String KEY_ALREADY_REGISTERED = "422.001";
    public static final String VALIDATION_FAILED = "422.002";
    public static final String EXCEEDED_REGISTERS_FOR_TYPE = "422.003";
    public static final String NOT_UPDATED_RANDOM_KEY = "422.003";

    public static final String UNEXPECTED_ERROR = "500.001";
}
