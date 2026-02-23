package com.techietester.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import com.techietester.app.App;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Abstract base class for all component (black-box) tests.
 *
 * <p>Boots the full Spring Boot application on a random port with the "test"
 * profile active. Provides a pre-configured REST Assured
 * {@link RequestSpecification} (stored in {@code spec}) that every subclass
 * can use directly:
 * <ul>
 *   <li>Base URI  : {@code http://localhost:{randomPort}}</li>
 *   <li>Base path : {@code /app}  (Jersey application path)</li>
 *   <li>Auth      : HTTP Basic – username {@code test} / password {@code test}</li>
 *   <li>Content-Type &amp; Accept : {@code application/json}</li>
 *   <li>Filters   : full request + response logging to stdout</li>
 * </ul>
 *
 * <p>No mocks are used – the real H2 database and the real Spring Security
 * filter chain are active for every test.
 */
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseApiTest {

    // Credentials matching the in-memory user defined in SecurityConfig
    protected static final String TEST_USERNAME = "test";
    protected static final String TEST_PASSWORD = "test";

    @LocalServerPort
    private int port;

    /**
     * Shared REST Assured request specification rebuilt before every test.
     * Subclasses call {@code given(spec)} to start a request.
     */
    protected RequestSpecification spec;

    @BeforeEach
    void setUpSpec() {
        String encoded = Base64.getEncoder()
                .encodeToString((TEST_USERNAME + ":" + TEST_PASSWORD)
                        .getBytes(StandardCharsets.UTF_8));

        spec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .setBasePath("/app")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Basic " + encoded)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}
