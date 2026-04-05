package com.ai.tester.actions.api.post;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public interface PostVideoGameActions {

    Response post(Object body, ContentType contentType);

    Response postWithoutAuth(Object body, ContentType contentType);

    Response postWithWrongAuth(Object body, ContentType contentType);
}

