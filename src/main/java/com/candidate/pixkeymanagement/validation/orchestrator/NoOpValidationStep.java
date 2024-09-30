package com.candidate.pixkeymanagement.validation.orchestrator;


import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.interfaces.ValidationStep;

class NoOpValidationStep implements ValidationStep {

    @Override
    public PixKeyContext validation(PixKeyContext context) {
        return context;
    }

    @Override
    public void setNext(ValidationStep step) {}
}
