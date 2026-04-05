package com.ai.tester.actions.api.get.getAll;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public interface GetAllGamesActions {

    Response getAllGames(ContentType contentType);
}

