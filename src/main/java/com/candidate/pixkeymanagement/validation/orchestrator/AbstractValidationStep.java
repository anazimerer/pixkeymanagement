package com.candidate.pixkeymanagement.validation.orchestrator;

import com.candidate.pixkeymanagement.exception.UnexpectedException;
import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.interfaces.ValidationStep;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.candidate.pixkeymanagement.util.MessageConstant.UNEXPECTED_ERROR;

@Slf4j
public abstract class AbstractValidationStep implements ValidationStep {

    private ValidationStep next;

    @Override
    public final void setNext(ValidationStep step) {
        this.next = step;
    }

    @Override
    public final PixKeyContext validation(PixKeyContext message) {
        try {
            return validateAndApplyNext(message)
                    .map(validatedMessage -> next.validation(validatedMessage))
                    .orElseGet(() -> next.validation(message));
        } catch (Exception e) {
            log.error("Unexpected error during validation {}", message, e);
            throw new UnexpectedException(UNEXPECTED_ERROR);
        }
    }

    protected abstract Optional<PixKeyContext> validateAndApplyNext(PixKeyContext message);

}
