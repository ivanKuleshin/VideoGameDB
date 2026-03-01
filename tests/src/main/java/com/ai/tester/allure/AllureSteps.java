package com.ai.tester.allure;

import io.qameta.allure.Allure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.function.Executable;

import java.util.concurrent.Callable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AllureSteps {

    public static void logStep(Logger log, String stepName, Executable action) {
        log.info("Step: {}", stepName);
        Allure.step(stepName, action::execute);
    }

    public static <T> T logStepAndReturn(Logger log, String stepName, Callable<T> action) {
        log.info("Step: {}", stepName);
        return Allure.step(stepName, action::call);
    }
}

