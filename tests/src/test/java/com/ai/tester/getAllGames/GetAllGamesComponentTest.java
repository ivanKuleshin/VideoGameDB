package com.ai.tester.getAllGames;

import com.ai.tester.AllureSteps;
import com.ai.tester.model.api.GetAllGamesResponseModel;
import com.ai.tester.model.api.VideoGameApiModel;
import com.ai.tester.model.db.VideoGameDbModel;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.ai.tester.data.Endpoint.VIDEOGAMES;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("GetAllGames - Check possibility to get all games")
class GetAllGamesComponentTest extends GetAllGamesBaseTest {

    @Test
    @TmsLinks({
        @TmsLink("XSP-95"),
        @TmsLink("XSP-94"),
        @TmsLink("XSP-93"),
        @TmsLink("XSP-92"),
        @TmsLink("XSP-91")
    })
    @DisplayName("GetAllGames – Check that all games are returned successfully")
    void getAllVideoGamesPositiveTest() {
        // Given
        List<VideoGameDbModel> allVideoGames = AllureSteps.logStepAndReturn(log, "Get all video games from database", () -> {
            List<VideoGameDbModel> allVideoGamesList = dbClient.getAllVideoGames();
            Assertions.assertThat(allVideoGamesList)
                .as("Response should contain at least one game")
                .isNotEmpty();

            return allVideoGamesList;
        });

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to get all video games",
            () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> Assertions.assertThat(response.getStatusCode()).isEqualTo(200));


        AllureSteps.logStep(log, "Verify response list content matches database list content",
            () -> {
                GetAllGamesResponseModel allGamesResponseModel = response.as(GetAllGamesResponseModel.class);
                assertThat(allGamesResponseModel.getVideoGames().size())
                    .as("Response list size is not matched with database list size")
                    .isEqualTo(allVideoGames.size());

                List<VideoGameApiModel> expectedResponseList = prepareExpectedAllGamesResponseList(allVideoGames);
                assertThat(allGamesResponseModel.getVideoGames())
                    .as("Response list content is not matched with database list content")
                    .containsExactlyInAnyOrderElementsOf(expectedResponseList);
            });
    }

    private List<VideoGameApiModel> prepareExpectedAllGamesResponseList(List<VideoGameDbModel> allVideoGames) {
        return allVideoGames.stream()
            .map(dbModel -> new VideoGameApiModel(
                dbModel.getId(),
                dbModel.getName(),
                dbModel.getReleaseDateAsString(),
                dbModel.getReviewScore(),
                dbModel.getCategory(),
                dbModel.getRating()
            ))
            .toList();
    }
}
