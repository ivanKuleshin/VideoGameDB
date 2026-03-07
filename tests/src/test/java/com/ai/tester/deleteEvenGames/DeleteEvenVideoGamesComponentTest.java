package com.ai.tester.deleteEvenGames;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.DeleteEvenVideoGamesResponseModel;
import com.ai.tester.model.db.VideoGameDbModel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.ai.tester.data.Endpoint.DELETE_EVEN_GAMES;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("DeleteEvenGames - Delete all even games from database")
class DeleteEvenVideoGamesComponentTest extends DeleteEvenVideoGamesBaseTest {

    @Test
    @DisplayName("DeleteEvenGames – Even ID games are deleted and odd ID games remain in database")
    void deleteEvenVideoGamesPositiveTest() {
        // Given
        List<VideoGameDbModel> gamesToBeDeleted = AllureSteps.logStepAndReturn(log,
            "Fetch first even ID games to be deleted from database",
            () -> {
                List<VideoGameDbModel> games = dbClient.getAllVideoGames().stream()
                    .filter(game -> game.getId() % 2 == 0)
                    .limit(DELETE_LIMIT)
                    .toList();
                assertThat(games)
                    .as("Database should contain at least one even ID game before the request")
                    .isNotEmpty();
                return games;
            });

        int expectedDeletedCount = gamesToBeDeleted.size();
        String expectedStatus = String.format(EXPECTED_STATUS_TEMPLATE, expectedDeletedCount);

        try {
            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send DELETE request to remove even ID games",
                () -> httpClient.delete(DELETE_EVEN_GAMES.getPath(), ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body status message reports deleted count",
                () -> {
                    DeleteEvenVideoGamesResponseModel responseModel = response.as(DeleteEvenVideoGamesResponseModel.class);
                    assertThat(responseModel.getStatus())
                        .as("Response status message should be '%s'", expectedStatus)
                        .isEqualTo(expectedStatus);
                });

            AllureSteps.logStep(log, "Verify deleted even ID games are absent from database",
                () -> gamesToBeDeleted.forEach(game ->
                    commonSteps.verifyGameNotExistsInDatabase(log, game.getId())));

        } finally {
            AllureSteps.logStep(log, "Restore deleted even ID games in database",
                () -> gamesToBeDeleted.forEach(game -> dbClient.insertVideoGame(game)));
        }
    }
}

