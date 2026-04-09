package com.ai.tester.actions.api.post;

import com.ai.tester.actions.api.BaseApiActions;
import com.ai.tester.client.http.AuthType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAMES;

@Component
public class PostVideoGameApiActions extends BaseApiActions implements PostVideoGameActions {

    @Override
    public Response post(Object body, ContentType contentType) {
        return sendPost(VIDEOGAMES.getPath(), body, contentType, AuthType.DEFAULT);
    }

    @Override
    public Response postWithoutAuth(Object body, ContentType contentType) {
        return sendPost(VIDEOGAMES.getPath(), body, contentType, AuthType.NONE);
    }

    @Override
    public Response postWithWrongAuth(Object body, ContentType contentType) {
        return sendPost(VIDEOGAMES.getPath(), body, contentType, AuthType.WRONG);
    }
}
