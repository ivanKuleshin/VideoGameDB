package com.techietester.base;

import com.techietester.app.App;
import com.techietester.client.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

/**
 * Abstract base class for all component (black-box) tests.
 *
 * <p>Boots the full Spring Boot application on a random port with the "test"
 * profile active.  Initialises the {@link HttpClient} singleton before every
 * test with:
 * <ul>
 *   <li>Base URI  : {@code http://localhost:{randomPort}}</li>
 *   <li>Base path : {@code /app}  (Jersey application path)</li>
 *   <li>Auth      : HTTP Basic – username {@code test} / password {@code test}</li>
 * </ul>
 *
 * <p>No mocks are used – the real H2 database and the real Spring Security
 * filter chain are active for every test.
 */
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseApiTest {

    private static final String BASE_URI   = "http://localhost";
    private static final String BASE_PATH  = "/app";
    private static final String USERNAME   = "test";
    private static final String PASSWORD   = "test";

    @LocalServerPort
    private int port;

    /**
     * Shared HTTP client available to every subclass.
     * Configured fresh before each test so port changes are always reflected.
     */
    protected HttpClient httpClient;

    @BeforeEach
    void setUpHttpClient() {
        httpClient = HttpClient.getInstance();
        httpClient.init(BASE_URI, port, BASE_PATH, USERNAME, PASSWORD);
    }
}
