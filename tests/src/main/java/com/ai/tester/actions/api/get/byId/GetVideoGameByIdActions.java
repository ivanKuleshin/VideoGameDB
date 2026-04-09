package com.ai.tester.actions.api.get.byId;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public interface GetVideoGameByIdActions {

    Response getById(int id, ContentType contentType);

    Response getByIdWithoutAuth(int id, ContentType contentType);

    Response getByIdWithWrongAuth(int id, ContentType contentType);

    Response getByInvalidId(String invalidId, ContentType contentType);
}