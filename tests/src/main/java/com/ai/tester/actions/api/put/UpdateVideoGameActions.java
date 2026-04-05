package com.ai.tester.actions.api.put;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public interface UpdateVideoGameActions {

    Response put(int id, Object body, ContentType contentType);

    Response putWithoutAuth(int id, Object body, ContentType contentType);

    Response putWithWrongAuth(int id, Object body, ContentType contentType);

    Response putByInvalidId(String invalidId, Object body, ContentType contentType);
}

