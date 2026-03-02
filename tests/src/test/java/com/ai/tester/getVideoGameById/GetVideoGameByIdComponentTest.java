package com.ai.tester.getVideoGameById;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.XmlUtil;
import io.qameta.allure.TmsLink;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.ai.tester.data.Endpoint.VIDEOGAME_BY_ID;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("GetVideoGameById - Check possibility to get a single video game")
class GetVideoGameByIdComponentTest extends GetVideoGameByIdBaseTest {

    @Test
    @TmsLink("XSP-99")
    @DisplayName("GetVideoGameById – Successful response returns HTTP 200")
    void getVideoGameByIdPositiveTest() {
        // Given
        VideoGameDbModel videoGame =
            AllureSteps.logStepAndReturn(log, "Get first video game from database",
                this::getFirstVideoGameFromDatabase);

        Integer gameId = videoGame.getId();

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game by ID",
            () -> httpClient.get(String.format(VIDEOGAME_BY_ID.getPath(), gameId), ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> Assertions.assertThat(response.getStatusCode())
                .as("Response status code should be 200 OK")
                .isEqualTo(200));

        AllureSteps.logStep(log, "Verify response body matches database record",
            () -> {
                VideoGameApiModel actualResponse = response.as(VideoGameApiModel.class);
                VideoGameApiModel expectedResponse = prepareExpectedVideoGameResponse(videoGame);

                assertThat(actualResponse)
                    .as("Response should match database record for selected video game")
                    .isEqualTo(expectedResponse);
            });
    }

    @Test
    @TmsLink("XSP-102")
    @DisplayName("GetVideoGameById – Response is valid XML when Accept: application/xml")
    void getVideoGameByIdXmlResponseTest() {
        // Given
        VideoGameDbModel videoGame =
            AllureSteps.logStepAndReturn(log, "Get first video game from database",
                this::getFirstVideoGameFromDatabase);

        Integer gameId = videoGame.getId();

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game by ID with Accept: application/xml",
            () -> httpClient.get(String.format(VIDEOGAME_BY_ID.getPath(), gameId), ContentType.XML));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> Assertions.assertThat(response.getStatusCode())
                .as("Response status code should be 200 OK")
                .isEqualTo(200));

        AllureSteps.logStep(log, "Verify response Content-Type is application/xml",
            () -> Assertions.assertThat(response.getContentType())
                .as("Response Content-Type should be application/xml")
                .contains("application/xml"));

        AllureSteps.logStep(log, "Verify response body is valid XML matching database record",
            () -> {
                String responseBody = response.getBody().asString();
                VideoGameXmlModel actualXmlResponse = XmlUtil.parse(responseBody, VideoGameXmlModel.class);
                VideoGameXmlModel expectedXmlResponse = prepareExpectedVideoGameXmlResponse(videoGame);

                assertThat(actualXmlResponse)
                    .as("XML response should match database record for selected video game")
                    .isEqualTo(expectedXmlResponse);
            });
    }
}
