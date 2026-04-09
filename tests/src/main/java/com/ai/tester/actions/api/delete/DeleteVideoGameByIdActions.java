package com.ai.tester.actions.api.delete;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public interface DeleteVideoGameByIdActions {

    Response deleteById(int id, ContentType contentType);
}
