package com.ai.tester.actions.api.get.byId;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;

@Component
@RequiredArgsConstructor
public class GetVideoGameByIdApiActions implements GetByIdApiActions {

    private final HttpClient httpClient;

    @Override
    public Response getById(int id, ContentType contentType) {
        return sendGet(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.DEFAULT);
    }

    @Override
    public Response getByIdWithoutAuth(int id, ContentType contentType) {
        return sendGet(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.NONE);
    }

    @Override
    public Response getByIdWithWrongAuth(int id, ContentType contentType) {
        return sendGet(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.WRONG);
    }

    @Override
    public Response getByInvalidId(String invalidId, ContentType contentType) {
        return sendGet(VIDEOGAME_BY_ID.getPath().formatted(invalidId), contentType, AuthType.DEFAULT);
    }

    private Response sendGet(String path, ContentType contentType, AuthType authType) {
        return httpClient.get(path, contentType, authType);
    }
}
