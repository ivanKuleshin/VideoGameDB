package com.ai.tester.actions.api.delete;

import com.ai.tester.actions.api.BaseApiActions;
import com.ai.tester.client.http.AuthType;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static com.ai.tester.data.Endpoint.DELETE_EVEN_GAMES;

@Component
public class DeleteEvenGamesApiActions extends BaseApiActions implements DeleteEvenGamesActions {

    @Override
    public Response deleteEvenGames(ContentType contentType) {
        return sendDelete(DELETE_EVEN_GAMES.getPath(), contentType, AuthType.DEFAULT);
    }
}
