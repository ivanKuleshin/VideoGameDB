package com.ai.tester.actions.api.delete;

import com.ai.tester.client.http.AuthType;
import com.ai.tester.client.http.HttpClient;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.DELETE_EVEN_GAMES;

@Component
@RequiredArgsConstructor
public class DeleteEvenGamesApiActions {

    private final HttpClient httpClient;

    public Response deleteEvenGames(ContentType contentType) {
        return httpClient.delete(DELETE_EVEN_GAMES.getPath(), contentType, AuthType.DEFAULT);
    }
}

