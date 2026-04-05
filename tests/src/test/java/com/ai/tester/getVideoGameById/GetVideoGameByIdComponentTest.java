package com.ai.tester.getVideoGameById;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.XmlUtil;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("GetVideoGameById - Check possibility to get a single video game")
class GetVideoGameByIdComponentTest extends GetVideoGameByIdBaseTest {

    @Test
    @TmsLinks({@TmsLink("XSP-99"), @TmsLink("XSP-100"), @TmsLink("XSP-101")})
    @DisplayName("GetVideoGameById - JSON response for existing game by ID")
    void getVideoGameByIdPositiveTest() {
        // Given
        VideoGameDbModel videoGame =
            AllureSteps.logStepAndReturn(log, "Get first video game from database",
                this::getFirstVideoGameFromDatabase);

        Integer gameId = videoGame.getId();

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game by ID",
            () -> byIdApiActions.getById(gameId, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200 OK")
                .isEqualTo(HttpStatus.OK.value()));

        AllureSteps.logStep(log, "Verify response Content-Type is application/json",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should be application/json")
                .contains(ContentType.JSON.toString()));

        AllureSteps.logStep(log, "Verify response body matches the database record",
            () -> {
                VideoGameApiModel actualResponse = response.as(VideoGameApiModel.class);
                VideoGameApiModel expectedResponse = prepareExpectedVideoGameResponse(videoGame);

                assertThat(actualResponse)
                    .as("Response body should match the expected")
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
            () -> byIdApiActions.getById(gameId, ContentType.XML));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200 OK")
                .isEqualTo(HttpStatus.OK.value()));

        AllureSteps.logStep(log, "Verify response Content-Type is application/xml",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should be application/xml")
                .contains(ContentType.XML.toString()));

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

    @Test
    @TmsLink("XSP-103")
    @DisplayName("GetVideoGameById - Request without authentication credentials")
    void getVideoGameByIdWithMissingCredentialsTest() {
        // Given
        VideoGameDbModel videoGame =
            AllureSteps.logStepAndReturn(log, "Get first video game from database",
                this::getFirstVideoGameFromDatabase);

        Integer gameId = videoGame.getId();

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request without authentication credentials",
            () -> byIdApiActions.getByIdWithoutAuth(gameId, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 401 Unauthorized when credentials are missing")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        AllureSteps.logStep(log, "Verify WWW-Authenticate header is present in the response",
            () -> assertThat(response.getHeader("WWW-Authenticate"))
                .as("WWW-Authenticate header should be present in 401 response")
                .isNotEmpty());
    }

    @Test
    @TmsLink("XSP-104")
    @DisplayName("GetVideoGameById - Request with invalid authentication credentials")
    void getVideoGameByIdWithInvalidCredentialsTest() {
        // Given
        VideoGameDbModel videoGame =
            AllureSteps.logStepAndReturn(log, "Get first video game from database",
                this::getFirstVideoGameFromDatabase);

        Integer gameId = videoGame.getId();

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request with invalid authentication credentials",
            () -> byIdApiActions.getByIdWithWrongAuth(gameId, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 401 Unauthorized when credentials are invalid")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        AllureSteps.logStep(log, "Verify WWW-Authenticate header is present in the response",
            () -> assertThat(response.getHeader("WWW-Authenticate"))
                .as("WWW-Authenticate header should be present in 401 response")
                .isNotEmpty());
    }

    @Test
    @TmsLink("XSP-105")
    @DisplayName("GetVideoGameById - Request for non-existent game ID")
    @Disabled("XSP-105: app returns 500 due to missing 404 guard — enable after app fix")
    void getVideoGameByIdNonExistentReturns500Test() {
        // Given
        AllureSteps.logStep(log, "Confirm video game with non-existent ID is absent from database",
            () -> assertThat(dbClient.getVideoGameById(NON_EXISTING_GAME_ID))
                .as("Game with ID %d should not exist in the database", NON_EXISTING_GAME_ID)
                .isEmpty());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request for non-existent video game ID",
            () -> byIdApiActions.getById(NON_EXISTING_GAME_ID, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 500 Internal Server Error",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 500 Internal Server Error for non-existent ID")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    @TmsLink("XSP-106")
    @DisplayName("GetVideoGameById - Request with non-integer path parameter")
    void getVideoGameByIdWithNonIntegerIdTest() {
        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request with non-integer path parameter",
            () -> byIdApiActions.getByInvalidId(NON_INTEGER_GAME_ID, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 400 or 404",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 400 or 404 for non-integer path parameter")
                .isIn(HttpStatus.BAD_REQUEST.value(), HttpStatus.NOT_FOUND.value()));

        AllureSteps.logStep(log, "Verify response Content-Type is application/json",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should be application/json")
                .contains(ContentType.JSON.toString()));
    }
}
