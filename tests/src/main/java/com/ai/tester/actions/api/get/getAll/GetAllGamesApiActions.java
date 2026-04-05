package com.ai.tester.actions.api.get.getAll;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.VIDEOGAMES;

@Component
@RequiredArgsConstructor
public class GetAllGamesApiActions implements GetAllGamesActions {

    private final HttpClient httpClient;

    @Override
    public Response getAllGames(ContentType contentType) {
        return httpClient.get(VIDEOGAMES.getPath(), contentType, AuthType.DEFAULT);
    }
}

