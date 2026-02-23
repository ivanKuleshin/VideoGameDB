package com.ai.tester;

import com.ai.tester.app.App;
import com.ai.tester.client.HttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiTest {

    @Value("${base.url}")
    private String baseUrl;

    private static final String BASE_PATH = "/app";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    @LocalServerPort
    private int port;

    protected HttpClient httpClient;

    @BeforeAll
    void setUpHttpClient() {
        httpClient = HttpClient.getInstance();
        httpClient.init(baseUrl, port, BASE_PATH, USERNAME, PASSWORD);
    }
}
