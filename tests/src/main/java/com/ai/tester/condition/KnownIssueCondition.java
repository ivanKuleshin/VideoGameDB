package com.ai.tester.condition;

import com.ai.tester.annotation.KnownIssue;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public class KnownIssueCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Optional<KnownIssue> knownIssue = context.getElement()
            .map(element -> element.getAnnotation(KnownIssue.class));

        return knownIssue
            .map(annotation -> ConditionEvaluationResult.disabled(
                "Skipped due to known issue: " + annotation.value()))
            .orElseGet(() -> ConditionEvaluationResult.enabled("No known issue"));
    }
}

