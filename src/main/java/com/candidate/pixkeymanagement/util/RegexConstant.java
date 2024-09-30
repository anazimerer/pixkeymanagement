package com.candidate.pixkeymanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class RegexConstant {

    public static final String COUNTRY_CODE_REGEX = "\\+\\d{1,2}";
    public static final String ONLY_NUMBER_REGEX = "\\d+";
    public static final String TWO_OR_THREE_DIGITS_REGEX = "\\d{2,3}";

}