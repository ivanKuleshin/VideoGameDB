package com.techietester.client;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;

/**
 * Thread-safe singleton HTTP client for component tests.
 *
 * <p>Encapsulates all REST Assured configuration (base URI, port, auth headers,
 * content-type, logging filters).  Exposes generic {@code get}, {@code post},
 * {@code put} and {@code delete} methods that operate on arbitrary paths and
 * bodies — no business knowledge lives here.
 *
 * <p>The singleton must be initialised once per test run via
 * {@link #init(String, int, String, String, String)} before any test calls are made.
 * {@link com.techietester.base.BaseApiTest} is responsible for that call.
 *
 * <h3>Singleton pattern</h3>
 * <ul>
 *   <li>Private constructor — cannot be instantiated directly.</li>
 *   <li>Volatile instance field + double-checked locking ensures safe lazy
 *       initialisation under concurrent access.</li>
 *   <li>{@link #init} rebuilds the underlying {@link RequestSpecification}
 *       every time it is called, which lets {@link com.techietester.base.BaseApiTest}
 *       refresh the port when {@code @SpringBootTest} picks a new random
 *       port for a new test class.</li>
 * </ul>
 */
public final class HttpClient {

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static volatile HttpClient instance;

    private HttpClient() {}

    /**
     * Returns the single shared {@link HttpClient} instance, creating it on
     * the first call (double-checked locking).
     */
    public static HttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = new HttpClient();
                }
            }
        }
        return instance;
    }

    // ── Internal state ────────────────────────────────────────────────────────

    /** Base request spec rebuilt on every {@link #init} call. */
    private volatile RequestSpecification spec;

    // ── Initialisation ────────────────────────────────────────────────────────

    /**
     * Configures the client.  Must be called before the first request.
     * Safe to call multiple times (e.g. when the port changes between test
     * classes in the same JVM run).
     *
     * @param baseUri  scheme + host, e.g. {@code "http://localhost"}
     * @param port     server port (typically the random port from {@code @LocalServerPort})
     * @param basePath root path of the API, e.g. {@code "/app"}
     * @param username HTTP Basic auth username
     * @param password HTTP Basic auth password
     */
    public void init(String baseUri, int port, String basePath,
                     String username, String password) {
        String encoded = Base64.getEncoder()
                .encodeToString((username + ":" + password)
                        .getBytes(StandardCharsets.UTF_8));

        spec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setPort(port)
                .setBasePath(basePath)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("Authorization", "Basic " + encoded)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }

    // ── HTTP verbs ────────────────────────────────────────────────────────────

    /**
     * Sends a {@code GET} request to the given path.
     *
     * @param path relative path appended to the base path, e.g. {@code "/videogames/1"}
     * @return the raw REST Assured {@link Response}
     */
    public Response get(String path) {
        return given(spec)
                .get(path);
    }

    /**
     * Sends a {@code POST} request with a JSON-serialisable body.
     *
     * @param path relative path
     * @param body request body (serialised to JSON by REST Assured)
     * @return the raw REST Assured {@link Response}
     */
    public Response post(String path, Object body) {
        return given(spec)
                .body(body)
                .post(path);
    }

    /**
     * Sends a {@code PUT} request with a JSON-serialisable body.
     *
     * @param path relative path (may contain path-param segments)
     * @param body request body
     * @return the raw REST Assured {@link Response}
     */
    public Response put(String path, Object body) {
        return given(spec)
                .body(body)
                .put(path);
    }

    /**
     * Sends a {@code DELETE} request to the given path.
     *
     * @param path relative path
     * @return the raw REST Assured {@link Response}
     */
    public Response delete(String path) {
        return given(spec)
                .delete(path);
    }
}

