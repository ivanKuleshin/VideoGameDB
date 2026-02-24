package com.ai.tester.getAllGames;

import com.ai.tester.model.VideoGameDbModel;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ai.tester.data.Endpoint.VIDEOGAMES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("GetAllGames - Check possibility to get all games")
class GetAllGamesComponentTest extends GetAllGamesBaseTest {

    @Test
    @DisplayName("GetAllGames – Check that all games are returned successfully")
    void getAllVideoGamesPositiveTest() {
        httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON)
            .then()
            .statusCode(200)
            .body("videoGame", notNullValue())
            .body("videoGame.size()", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("GetAllGames – Check that the number of returned games matches the database")
    void getAllVideoGamesCountMatchesDbTest() {
        List<VideoGameDbModel> gamesInDb = dbClient.getAllVideoGames();

        httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON)
            .then()
            .statusCode(200)
            .body("videoGame.size()", equalTo(gamesInDb.size()));
    }

    @Test
    @DisplayName("GetAllGames – Check that returned game names match the database")
    void getAllVideoGamesNamesMatchDbTest() {
        List<String> namesInDb = dbClient.getAllVideoGames()
            .stream()
            .map(VideoGameDbModel::getName)
            .toList();

        List<String> namesFromApi = httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON)
            .jsonPath()
            .getList("videoGame.name", String.class);

        assertThat(namesFromApi).containsExactlyInAnyOrderElementsOf(namesInDb);
    }
}
