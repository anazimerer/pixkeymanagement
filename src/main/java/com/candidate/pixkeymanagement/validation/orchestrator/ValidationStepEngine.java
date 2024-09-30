package com.candidate.pixkeymanagement.validation.orchestrator;


import com.candidate.pixkeymanagement.validation.PixKeyContext;
import com.candidate.pixkeymanagement.validation.interfaces.ChainElement;
import com.candidate.pixkeymanagement.validation.interfaces.ValidationStep;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationStepEngine {

    private final ValidationStep chainHead;

    public ValidationStepEngine(List<ValidationStep> steps) {
        this.chainHead = ChainElement.buildChain(steps, new NoOpValidationStep());
    }

    public PixKeyContext validation(PixKeyContext message) {
        return chainHead.validation(message);
    }
}