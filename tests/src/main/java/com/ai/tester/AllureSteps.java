package com.ai.tester;

import io.qameta.allure.Allure;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.function.Executable;

import java.util.concurrent.Callable;

public final class AllureSteps {

    private AllureSteps() {
    }

    public static void logStep(Logger log, String stepName, Executable action) {
        log.info("Step: {}", stepName);
        Allure.step(stepName, action::execute);
    }

    public static <T> T logStepAndReturn(Logger log, String stepName, Callable<T> action) {
        log.info("Step: {}", stepName);
        return Allure.step(stepName, action::call);
    }
}

