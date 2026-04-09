package com.ai.tester.actions.api.put;

import com.ai.tester.actions.api.BaseApiActions;
import com.ai.tester.client.http.AuthType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;

@Component
public class UpdateVideoGameApiActions extends BaseApiActions implements UpdateVideoGameActions {

    @Override
    public Response put(int id, Object body, ContentType contentType) {
        return sendPut(pathById(id), body, contentType, AuthType.DEFAULT);
    }

    @Override
    public Response putWithoutAuth(int id, Object body, ContentType contentType) {
        return sendPut(pathById(id), body, contentType, AuthType.NONE);
    }

    @Override
    public Response putWithWrongAuth(int id, Object body, ContentType contentType) {
        return sendPut(pathById(id), body, contentType, AuthType.WRONG);
    }

    @Override
    public Response putByInvalidId(String invalidId, Object body, ContentType contentType) {
        return sendPut(pathById(invalidId), body, contentType, AuthType.DEFAULT);
    }

    private String pathById(Object id) {
        return VIDEOGAME_BY_ID.getPath().formatted(id);
    }
}
