package com.ai.tester.actions.api.delete;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;

@Component
@RequiredArgsConstructor
public class DeleteVideoGameApiActions implements DeleteByIdActions {

    private final HttpClient httpClient;

    @Override
    public Response deleteById(int id, ContentType contentType) {
        return sendDelete(VIDEOGAME_BY_ID.getPath().formatted(id), contentType);
    }

    private Response sendDelete(String path, ContentType contentType) {
        return httpClient.delete(path, contentType, AuthType.DEFAULT);
    }
}
