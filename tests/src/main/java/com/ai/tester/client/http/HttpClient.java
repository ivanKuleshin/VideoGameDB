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
    }

    public Response get(String path, ContentType contentType) {
        log.debug("GET path={}, contentType={}", path, contentType);
        return given(spec)
            .accept(contentType)
            .get(path);
    }

    public Response post(String path, Object body, ContentType contentType) {
        log.debug("POST path={}, contentType={}, body={}", path, contentType, body);
        return given(spec)
            .accept(contentType)
            .contentType(contentType)
            .body(body)
            .post(path);
    }

    public Response put(String path, Object body, ContentType contentType) {
        log.debug("PUT path={}, contentType={}, body={}", path, contentType, body);
        return given(spec)
            .contentType(contentType)
            .accept(contentType)
            .body(body)
            .put(path);
    }

    public Response delete(String path, ContentType contentType) {
        log.debug("DELETE path={}, contentType={}", path, contentType);
        return given(spec)
            .accept(contentType)
            .delete(path);
    }
}
