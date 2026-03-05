package com.ai.tester.getAllGames;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.GetAllGamesResponseModel;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.GetAllGamesXmlResponseModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.XmlUtil;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
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
        List<VideoGameDbModel> allVideoGames =
            AllureSteps.logStepAndReturn(log, "Get all video games from database", () -> {
                List<VideoGameDbModel> allVideoGamesList = dbClient.getAllVideoGames();
                assertThat(allVideoGamesList)
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
            () -> assertThat(response.getStatusCode()).isEqualTo(200));


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

    @Test
    @TmsLink("XSP-96")
    @DisplayName("GetAllGames – Response is valid XML when Accept: application/xml")
    void getAllVideoGamesXmlResponseTest() {
        // Given
        List<VideoGameDbModel> allVideoGames =
            AllureSteps.logStepAndReturn(log, "Get all video games from database", () -> {
                List<VideoGameDbModel> allVideoGamesList = dbClient.getAllVideoGames();
                assertThat(allVideoGamesList)
                    .as("Response should contain at least one game")
                    .isNotEmpty();

                return allVideoGamesList;
            });

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to get games with Accept: application/xml header",
            () -> httpClient.get(VIDEOGAMES.getPath(), ContentType.XML));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200")
                .isEqualTo(200));

        AllureSteps.logStep(log, "Verify response Content-Type is application/xml",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should be application/xml")
                .contains("application/xml"));

        AllureSteps.logStep(log,
            "Verify response body is valid XML with <videoGames> root element containing <videoGame> children",
            () -> {
                GetAllGamesXmlResponseModel xmlResponse =
                    XmlUtil.parse(response.getBody().asString(), GetAllGamesXmlResponseModel.class);

                assertThat(xmlResponse.getVideoGames())
                    .as("XML <videoGames> should contain at least one <videoGame> child element")
                    .isNotEmpty();

                List<VideoGameXmlModel> expectedList = prepareExpectedAllGamesXmlResponseList(allVideoGames);

                assertThat(xmlResponse.getVideoGames())
                    .as("XML response list content should match database list content")
                    .containsExactlyInAnyOrderElementsOf(expectedList);
            });
    }

}
