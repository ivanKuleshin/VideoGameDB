package com.ai.tester.actions.api;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseApiActions {

    @Autowired
    protected HttpClient httpClient;

    protected Response sendGet(String path, ContentType contentType, AuthType authType) {
        return httpClient.get(path, contentType, authType);
    }

    protected Response sendPost(String path, Object body, ContentType contentType, AuthType authType) {
        return httpClient.post(path, body, contentType, authType);
    }

    protected Response sendPut(String path, Object body, ContentType contentType, AuthType authType) {
        return httpClient.put(path, body, contentType, authType);
    }

    protected Response sendDelete(String path, ContentType contentType, AuthType authType) {
        return httpClient.delete(path, contentType, authType);
    }
}
