package com.ai.tester.deleteVideoGame;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.DeleteVideoGameResponseModel;
import com.ai.tester.model.api.json.GetAllGamesResponseModel;
import com.ai.tester.model.api.json.VideoGameApiModel;
import io.qameta.allure.TmsLink;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ai.tester.data.Endpoint.VIDEOGAMES;
import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("DeleteVideoGame - Check possibility to delete a game by ID")
class DeleteVideoGameComponentTest extends DeleteVideoGameBaseTest {

    @AfterEach
    void cleanUp() {
        dbClient.deleteVideoGameById(PRIMARY_GAME.getId());
        dbClient.deleteVideoGameById(SECONDARY_GAME.getId());
    }

    @Test
    @TmsLink("XSP-125")
    @DisplayName("DeleteVideoGame – Happy path delete existing game returns 200 and success JSON body")
    void deleteExistingVideoGameReturns200WithSuccessBodyTest() {
        // Given
        AllureSteps.logStep(log, "Insert test video game: " + PRIMARY_GAME.getName(),
            () -> dbClient.insertVideoGame(PRIMARY_GAME.getGameData()));

        commonSteps.verifyGameExistsInDatabase(log, dbClient, PRIMARY_GAME.getId(), PRIMARY_GAME.getName());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Delete video game with ID " + PRIMARY_GAME.getId(),
            () -> httpClient.delete(String.format(VIDEOGAME_BY_ID.getPath(), PRIMARY_GAME.getId()), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200")
                .isEqualTo(200));

        AllureSteps.logStep(log, "Verify response body contains status 'Record Deleted Successfully'",
            () -> {
                DeleteVideoGameResponseModel responseModel = response.as(DeleteVideoGameResponseModel.class);
                assertThat(responseModel.getStatus())
                    .as("Response status message should be '%s'", EXPECTED_DELETE_STATUS)
                    .isEqualTo(EXPECTED_DELETE_STATUS);
            });

        commonSteps.verifyGameNotExistsInDatabase(log, dbClient, PRIMARY_GAME.getId());
    }

    @Test
    @TmsLink("XSP-126")
    @DisplayName("DeleteVideoGame – DB record is absent from GET all games list after deletion")
    void deleteVideoGameIsAbsentFromGetAllGamesTest() {
        // Given
        AllureSteps.logStep(log, "Insert test video game: " + SECONDARY_GAME.getName(),
            () -> dbClient.insertVideoGame(SECONDARY_GAME.getGameData()));

        commonSteps.verifyGameExistsInDatabase(log, dbClient, SECONDARY_GAME.getId(), SECONDARY_GAME.getName());

        // When
        AllureSteps.logStep(log, "Delete video game with ID " + SECONDARY_GAME.getId(),
            () -> httpClient.delete(String.format(VIDEOGAME_BY_ID.getPath(), SECONDARY_GAME.getId()), ContentType.JSON));

        Response getAllResponse = AllureSteps.logStepAndReturn(log,
            "Retrieve all video games",
            () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(getAllResponse.getStatusCode())
                .as("Response status code should be 200")
                .isEqualTo(200));

        AllureSteps.logStep(log, "Verify deleted game with ID " + SECONDARY_GAME.getId() + " is absent from response list",
            () -> {
                GetAllGamesResponseModel allGamesResponse = getAllResponse.as(GetAllGamesResponseModel.class);
                List<Integer> gameIds = allGamesResponse.getVideoGames().stream()
                    .map(VideoGameApiModel::getId)
                    .toList();

                assertThat(gameIds)
                    .as("Response list should not contain game with ID %d", SECONDARY_GAME.getId())
                    .doesNotContain(SECONDARY_GAME.getId());
            });

        commonSteps.verifyGameNotExistsInDatabase(log, dbClient, SECONDARY_GAME.getId());
    }
}

