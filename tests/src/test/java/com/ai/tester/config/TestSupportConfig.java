package com.ai.tester.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.ai.tester.actions",
    "com.ai.tester.steps"
})
public class TestSupportConfig {
}
