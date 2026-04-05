package com.ai.tester.actions.api.put;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;

@Component
@RequiredArgsConstructor
public class UpdateVideoGameApiActions implements UpdateVideoGameActions {

    private final HttpClient httpClient;

    @Override
    public Response put(int id, Object body, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), body, contentType, AuthType.DEFAULT);
    }

    @Override
    public Response putWithoutAuth(int id, Object body, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), body, contentType, AuthType.NONE);
    }

    @Override
    public Response putWithWrongAuth(int id, Object body, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(id), body, contentType, AuthType.WRONG);
    }

    @Override
    public Response putByInvalidId(String invalidId, Object body, ContentType contentType) {
        return send(VIDEOGAME_BY_ID.getPath().formatted(invalidId), body, contentType, AuthType.DEFAULT);
    }

    private Response send(String path, Object body, ContentType contentType, AuthType authType) {
        return httpClient.put(path, body, contentType, authType);
    }
}

