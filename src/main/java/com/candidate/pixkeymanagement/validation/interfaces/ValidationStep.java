package com.candidate.pixkeymanagement.validation.interfaces;


import com.candidate.pixkeymanagement.validation.PixKeyContext;

public interface ValidationStep extends ChainElement<ValidationStep> {

    PixKeyContext validation(PixKeyContext context);

}