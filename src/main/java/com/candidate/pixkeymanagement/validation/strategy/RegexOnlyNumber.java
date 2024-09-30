package com.candidate.pixkeymanagement.validation.strategy;

import static com.candidate.pixkeymanagement.util.RegexConstant.ONLY_NUMBER_REGEX;

public class RegexOnlyNumber implements ValidateRegexStrategy {

    @Override
    public boolean validate(String key) {
        return key.matches(ONLY_NUMBER_REGEX);
    }

}
