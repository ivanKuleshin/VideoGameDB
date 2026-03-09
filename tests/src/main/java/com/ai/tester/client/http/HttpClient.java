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

    private HttpClient() {
    }

    private static final class Holder {
        private static final HttpClient INSTANCE = new HttpClient();
    }

    public static HttpClient getInstance() {
        return Holder.INSTANCE;
    }

    private RequestSpecification spec;
    private RequestSpecification noAuthSpec;

    private static PrintStream log4jPrintStream() {
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

    public void init(String baseUri, int port, String basePath,
                     String username, String password) {
        log.debug("Initialising HttpClient: baseUri={}, port={}, basePath={}", baseUri, port, basePath);
        PrintStream logStream = log4jPrintStream();
        spec = new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setPort(port)
            .setBasePath(basePath)
            .setAuth(preemptive().basic(username, password))
            .addFilter(new ErrorLoggingFilter(logStream))
            .build();
        noAuthSpec = new RequestSpecBuilder()
            .setBaseUri(baseUri)
            .setPort(port)
            .setBasePath(basePath)
            .addFilter(new ErrorLoggingFilter(logStream))
            .build();
    }

    private void checkInitialized() {
        if (spec == null || noAuthSpec == null) {
            throw new IllegalStateException("HttpClient.init() must be called before use");
        }
    }

    public Response get(String path, ContentType contentType) {
        checkInitialized();
        log.debug("GET path={}, contentType={}", path, contentType);
        return given(spec)
            .accept(contentType)
            .get(path);
    }

    public Response getWithoutAuth(String path, ContentType contentType) {
        checkInitialized();
        log.debug("GET without auth path={}, contentType={}", path, contentType);
        return given(noAuthSpec)
            .accept(contentType)
            .get(path);
    }

    public Response delete(String path, ContentType contentType) {
        checkInitialized();
        log.debug("DELETE path={}, contentType={}", path, contentType);
        return given(spec)
            .accept(contentType)
            .delete(path);
    }
}
