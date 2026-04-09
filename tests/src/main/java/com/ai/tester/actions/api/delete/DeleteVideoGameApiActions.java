package com.ai.tester.actions.api.delete;

import com.ai.tester.actions.api.BaseApiActions;
import com.ai.tester.client.http.AuthType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;

@Component
public class DeleteVideoGameApiActions extends BaseApiActions implements DeleteVideoGameByIdActions {

    @Override
    public Response deleteById(int id, ContentType contentType) {
        return sendDelete(VIDEOGAME_BY_ID.getPath().formatted(id), contentType, AuthType.DEFAULT);
    }
}
