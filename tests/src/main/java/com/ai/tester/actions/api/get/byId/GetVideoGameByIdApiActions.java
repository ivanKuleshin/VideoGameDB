package com.ai.tester.actions.api.get.byId;

import com.ai.tester.actions.api.BaseApiActions;
import com.ai.tester.client.http.AuthType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;

@Component
public class GetVideoGameByIdApiActions extends BaseApiActions implements GetVideoGameByIdActions {

    @Override
    public Response getById(int id, ContentType contentType) {
        return sendGet(pathById(id), contentType, AuthType.DEFAULT);
    }

    @Override
    public Response getByIdWithoutAuth(int id, ContentType contentType) {
        return sendGet(pathById(id), contentType, AuthType.NONE);
    }

    @Override
    public Response getByIdWithWrongAuth(int id, ContentType contentType) {
        return sendGet(pathById(id), contentType, AuthType.WRONG);
    }

    @Override
    public Response getByInvalidId(String invalidId, ContentType contentType) {
        return sendGet(pathById(invalidId), contentType, AuthType.DEFAULT);
    }

    private String pathById(Object id) {
        return VIDEOGAME_BY_ID.getPath().formatted(id);
    }
}
