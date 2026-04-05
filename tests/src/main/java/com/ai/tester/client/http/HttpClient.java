package com.ai.tester.client.http;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;

import java.io.OutputStream;
import java.io.PrintStream;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.preemptive;

@Log4j2
public final class HttpClient {

    private static final class Holder {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    private RequestSpecification spec;
    private RequestSpecification noAuthSpec;
    private RequestSpecification wrongAuthSpec;

    private HttpClient() {
    }

    public static HttpClient getInstance() {
        return Holder.INSTANCE;
    }

    public void init(String baseUri, int port, String basePath,
                     String username, String password,
                     String wrongUsername, String wrongPassword) {
        log.debug("Initialising HttpClient: baseUri={}, port={}, basePath={}", baseUri, port, basePath);
        PrintStream logStream = createLog4jPrintStream();
        spec = createAuthSpec(baseUri, port, basePath, username, password, logStream);
        noAuthSpec = createNoAuthSpec(baseUri, port, basePath, logStream);
        wrongAuthSpec = createAuthSpec(baseUri, port, basePath, wrongUsername, wrongPassword, logStream);
    }

    public Response get(String path, ContentType contentType, AuthType authType) {
        checkInitialized();
        log.debug("GET path={}, contentType={}, authType={}", path, contentType, authType);
        return given(resolveSpec(authType))
            .accept(contentType)
            .get(path);
    }

    public Response post(String path, Object body, ContentType contentType, AuthType authType) {
        checkInitialized();
        log.debug("POST path={}, contentType={}, authType={}", path, contentType, authType);
        return given(resolveSpec(authType))
            .contentType(contentType)
            .accept(contentType)
            .body(body)
            .post(path);
    }

    public Response put(String path, Object body, ContentType contentType, AuthType authType) {
        checkInitialized();
        log.debug("PUT path={}, contentType={}, authType={}", path, contentType, authType);
        return given(resolveSpec(authType))
            .contentType(contentType)
            .accept(contentType)
            .body(body)
            .put(path);
    }

    public Response delete(String path, ContentType contentType, AuthType authType) {
        checkInitialized();
        log.debug("DELETE path={}, contentType={}, authType={}", path, contentType, authType);
        return given(resolveSpec(authType))
            .accept(contentType)
            .delete(path);
    }

    private static PrintStream createLog4jPrintStream() {
        return new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                char c = (char) b;
                if (c == '\n') {
                    log.debug(buffer.toString());
                    buffer.setLength(0);
                } else {
                    buffer.append(c);
                }
            }
        }, true);
    }

    private static RequestSpecification createAuthSpec(String baseUri, int port, String basePath,
                                                       String username, String password,
                                                       PrintStream logStream) {
        return new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setPort(port)
            .setBasePath(basePath)
            .setAuth(preemptive().basic(username, password))
            .addFilter(new ErrorLoggingFilter(logStream))
            .build();
    }

    private static RequestSpecification createNoAuthSpec(String baseUri, int port, String basePath,
                                                         PrintStream logStream) {
        return new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setPort(port)
            .setBasePath(basePath)
            .addFilter(new ErrorLoggingFilter(logStream))
            .build();
    }

    private void checkInitialized() {
        if (spec == null || noAuthSpec == null || wrongAuthSpec == null) {
            throw new IllegalStateException("HttpClient.init() must be called before use");
        }
    }

    private RequestSpecification resolveSpec(AuthType authType) {
        return switch (authType) {
            case DEFAULT -> spec;
            case NONE -> noAuthSpec;
            case WRONG -> wrongAuthSpec;
        };
    }
}
