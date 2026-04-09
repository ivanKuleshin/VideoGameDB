package com.ai.tester.actions.api.delete;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public interface DeleteEvenGamesActions {
    Response deleteEvenGames(ContentType contentType);
}
