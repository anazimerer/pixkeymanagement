package com.candidate.pixkeymanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class OrderStepConstant {

    public static final Integer CELLPHONE_STEP_ORDER = 1;
    public static final Integer CPF_STEP_ORDER = 2;
    public static final Integer EMAIL_STEP_ORDER = 3;
    public static final Integer GENERAL_STEP_ORDER = 0;

}