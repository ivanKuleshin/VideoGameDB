package com.ai.tester.actions.api.post;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAMES;

@Component
@RequiredArgsConstructor
public class PostVideoGameApiActions implements PostVideoGameActions {

    private final HttpClient httpClient;

    @Override
    public Response post(Object body, ContentType contentType) {
        return send(body, contentType, AuthType.DEFAULT);
    }

    @Override
    public Response postWithoutAuth(Object body, ContentType contentType) {
        return send(body, contentType, AuthType.NONE);
    }

    @Override
    public Response postWithWrongAuth(Object body, ContentType contentType) {
        return send(body, contentType, AuthType.WRONG);
    }

    private Response send(Object body, ContentType contentType, AuthType authType) {
        return httpClient.post(VIDEOGAMES.getPath(), body, contentType, authType);
    }
}

