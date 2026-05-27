package com.ai.tester.deleteEvenGames;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.DeleteEvenVideoGamesResponseModel;
import com.ai.tester.model.db.VideoGameDbModel;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("DeleteEvenGames - Delete all even games from database")
class DeleteEvenVideoGamesComponentTest extends DeleteEvenVideoGamesBaseTest {

    @Test
    @TmsLinks({@TmsLink("XSP-151"), @TmsLink("XSP-152")})
    @DisplayName("DeleteEvenGames – Even ID games are deleted, response reports count, and DB state is correct")
    void deleteEvenVideoGamesPositiveTest() {
        // Given
        List<VideoGameDbModel> allGames = AllureSteps.logStepAndReturn(log,
            "Fetch all games from database before deletion",
            () -> dbClient.getAllVideoGames());

        List<VideoGameDbModel> evenGames = allGames.stream()
            .filter(game -> game.getId() % 2 == 0)
            .limit(DELETE_LIMIT)
            .toList();

        AllureSteps.logStep(log,
            String.format("Verify database contains %d even-ID games before deletion", DELETE_LIMIT),
            () -> assertThat(evenGames)
                .as("Database should contain exactly %d even ID games before the request", DELETE_LIMIT)
                .hasSize(DELETE_LIMIT));

        List<VideoGameDbModel> oddGames = allGames.stream()
            .filter(game -> game.getId() % 2 != 0)
            .toList();

        String expectedStatus = String.format(EXPECTED_STATUS_TEMPLATE, evenGames.size());

        try {
            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send DELETE request to remove even ID games",
                () -> apiActions.deleteEvenGames(ContentType.JSON));

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
                () -> evenGames.forEach(game ->
                    commonSteps.verifyGameNotExistsInDatabase(log, game.getId())));

            AllureSteps.logStep(log, "Verify odd ID games remain in database after deletion",
                () -> oddGames.forEach(game ->
                    commonSteps.verifyGameExistsInDatabase(log, game.getId(), game.getName())));

        } finally {
            AllureSteps.logStep(log, "Restore deleted even ID games in database",
                () -> evenGames.forEach(game -> dbClient.insertVideoGame(game)));
        }
    }
}
