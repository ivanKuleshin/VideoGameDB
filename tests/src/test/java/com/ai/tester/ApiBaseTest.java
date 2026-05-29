package com.ai.tester;

import com.ai.tester.app.App;
import com.ai.tester.client.db.DbClient;
import com.ai.tester.config.DbClientConfig;
import com.ai.tester.config.HttpClientConfig;
import com.ai.tester.config.TestSupportConfig;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.steps.CommonSteps;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {DbClientConfig.class, HttpClientConfig.class, TestSupportConfig.class})
public abstract class ApiBaseTest {

    @Autowired
    protected DbClient dbClient;

    @Autowired
    protected CommonSteps commonSteps;

    private static final int MAX_BASELINE_ID = 10;

    @AfterEach
    void cleanUpFixtureData() {
        for (VideoGameTestDataFixtures fixture : VideoGameTestDataFixtures.values()) {
            if (fixture.getId() > MAX_BASELINE_ID) {
                dbClient.deleteVideoGameById(fixture.getId());
            }
        }
    }
}
