package com.ai.tester.deleteVideoGame;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.DeleteVideoGameResponseModel;
import com.ai.tester.model.api.json.GetAllGamesResponseModel;
import com.ai.tester.model.api.json.VideoGameApiModel;
import io.qameta.allure.TmsLink;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("DeleteVideoGame – Check possibility to delete a game by ID")
class DeleteVideoGameComponentTest extends DeleteVideoGameBaseTest {

    @Test
    @TmsLink("XSP-125")
    @DisplayName("DeleteVideoGame – Happy path delete existing game returns 200 and success JSON body")
    void deleteExistingVideoGamePositiveTest() {
        // Given
        AllureSteps.logStep(log, "Insert primary test game into database",
            () -> dbClient.insertVideoGame(PRIMARY_GAME.getGameData()));

        commonSteps.verifyGameExistsInDatabase(log, PRIMARY_GAME.getId(), PRIMARY_GAME.getName());

        try {
            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send DELETE request for primary game",
                () -> apiActions.deleteById(PRIMARY_GAME.getId(), ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body contains status 'Record Deleted Successfully'",
                () -> {
                    DeleteVideoGameResponseModel responseModel = response.as(DeleteVideoGameResponseModel.class);
                    assertThat(responseModel.getStatus())
                        .as("Response status message should be '%s'", EXPECTED_DELETE_STATUS)
                        .isEqualTo(EXPECTED_DELETE_STATUS);
                });

            commonSteps.verifyGameNotExistsInDatabase(log, PRIMARY_GAME.getId());
        } finally {
            dbClient.deleteVideoGameById(PRIMARY_GAME.getId());
        }
    }

    @Test
    @TmsLink("XSP-126")
    @DisplayName("DeleteVideoGame – DB record is absent from GET all games list after deletion")
    void deletedVideoGameAbsentFromGetAllGamesTest() {
        // Given
        AllureSteps.logStep(log, "Insert secondary test game into database",
            () -> dbClient.insertVideoGame(SECONDARY_GAME.getGameData()));

        commonSteps.verifyGameExistsInDatabase(log, SECONDARY_GAME.getId(), SECONDARY_GAME.getName());

        try {
            // When
            AllureSteps.logStep(log, "Send DELETE request for secondary game",
                () -> apiActions.deleteById(SECONDARY_GAME.getId(), ContentType.JSON));

            Response getAllResponse = AllureSteps.logStepAndReturn(log,
                "Retrieve all video games",
                () -> getAllGamesApiActions.getAllGames(ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(getAllResponse.getStatusCode())
                    .as("Response status code should be 200")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify deleted game is absent from response list",
                () -> {
                    GetAllGamesResponseModel allGamesResponse = getAllResponse.as(GetAllGamesResponseModel.class);
                    List<Integer> gameIds = allGamesResponse.getVideoGames().stream()
                        .map(VideoGameApiModel::getId)
                        .toList();

                    assertThat(gameIds)
                        .as("Response list should not contain game with ID %d", SECONDARY_GAME.getId())
                        .doesNotContain(SECONDARY_GAME.getId());
                });

            commonSteps.verifyGameNotExistsInDatabase(log, SECONDARY_GAME.getId());
        } finally {
            dbClient.deleteVideoGameById(SECONDARY_GAME.getId());
        }
    }
}

