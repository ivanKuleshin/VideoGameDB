package com.ai.tester.getVideoGameById;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.annotation.KnownIssue;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.ErrorResponseXmlModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.XmlUtil;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import io.qameta.allure.TmsLinks;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.assertj.core.api.SoftAssertions;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("GetVideoGameById – Check possibility to get a single video game by ID")
class GetVideoGameByIdComponentTest extends GetVideoGameByIdBaseTest {

    @Test
    @TmsLinks({
        @TmsLink("XSP-99"),
        @TmsLink("XSP-100")
    })
    @DisplayName("GetVideoGameById – Valid credentials and existing ID return 200 with all fields matching DB record")
    void getVideoGameByIdPositiveTest() {
        // Given
        VideoGameDbModel expectedGame = AllureSteps.logStepAndReturn(log,
            "Fetch expected game from database",
            () -> dbClient.getVideoGameById(GAME_ID_FOR_STATUS_AND_BODY_TESTS).orElseThrow());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game by ID",
            () -> apiActions.getById(GAME_ID_FOR_STATUS_AND_BODY_TESTS, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200")
                .isEqualTo(HttpStatus.OK.value()));

        AllureSteps.logStep(log, "Verify response body contains all 6 fields matching the database record",
            () -> assertThat(response.as(VideoGameApiModel.class))
                .as("Response body should match the database record for game ID %d",
                    GAME_ID_FOR_STATUS_AND_BODY_TESTS)
                .isEqualTo(prepareExpectedVideoGameResponse(expectedGame)));
    }

    @Test
    @TmsLink("XSP-101")
    @DisplayName("GetVideoGameById – Accept: application/json returns JSON Content-Type and valid flat JSON object")
    void getVideoGameByIdJsonContentTypeTest() {
        // Given
        VideoGameDbModel expectedGame = AllureSteps.logStepAndReturn(log,
            "Fetch expected game from database",
            () -> dbClient.getVideoGameById(GAME_ID_FOR_CONTENT_TYPE_TESTS).orElseThrow());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game with Accept: application/json",
            () -> apiActions.getById(GAME_ID_FOR_CONTENT_TYPE_TESTS, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200")
                .isEqualTo(HttpStatus.OK.value()));

        AllureSteps.logStep(log, "Verify response Content-Type is application/json",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should contain application/json")
                .contains(MediaType.APPLICATION_JSON_VALUE));

        AllureSteps.logStep(log, "Verify response body is a valid flat JSON object matching the database record",
            () -> assertThat(response.as(VideoGameApiModel.class))
                .as("JSON response body should match the database record for game ID %d",
                    GAME_ID_FOR_CONTENT_TYPE_TESTS)
                .isEqualTo(prepareExpectedVideoGameResponse(expectedGame)));
    }

    @Test
    @TmsLink("XSP-102")
    @DisplayName("GetVideoGameById – Accept: application/xml returns XML Content-Type and valid XML with root <videoGame>")
    void getVideoGameByIdXmlContentTypeTest() {
        // Given
        VideoGameDbModel expectedGame = AllureSteps.logStepAndReturn(log,
            "Fetch expected game from database",
            () -> dbClient.getVideoGameById(GAME_ID_FOR_CONTENT_TYPE_TESTS).orElseThrow());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game with Accept: application/xml",
            () -> apiActions.getById(GAME_ID_FOR_CONTENT_TYPE_TESTS, ContentType.XML));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 200",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 200")
                .isEqualTo(HttpStatus.OK.value()));

        AllureSteps.logStep(log, "Verify response Content-Type is application/xml",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should contain application/xml")
                .contains(MediaType.APPLICATION_XML_VALUE));

        AllureSteps.logStep(log,
            "Verify response body is valid XML with <videoGame> root element matching the database record",
            () -> {
                VideoGameXmlModel xmlResponse = XmlUtil.parse(response.asString(), VideoGameXmlModel.class);
                assertThat(xmlResponse)
                    .as("XML response body should match the database record for game ID %d",
                        GAME_ID_FOR_CONTENT_TYPE_TESTS)
                    .isEqualTo(prepareExpectedVideoGameXmlResponse(expectedGame));
            });
    }

    @Test
    @TmsLink("XSP-103")
    @DisplayName("GetVideoGameById – Request without Authorization header returns 401 Unauthorized")
    void getVideoGameByIdWithoutAuthTest() {
        // Given
        AllureSteps.logStep(log,
            String.format("Verify game with ID %d exists in database", GAME_ID_FOR_STATUS_AND_BODY_TESTS),
            () -> dbClient.getVideoGameById(GAME_ID_FOR_STATUS_AND_BODY_TESTS).orElseThrow());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game without authentication credentials",
            () -> apiActions.getByIdWithoutAuth(GAME_ID_FOR_STATUS_AND_BODY_TESTS, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 401 when no Authorization header is provided")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @TmsLink("XSP-104")
    @DisplayName("GetVideoGameById – Request with wrong credentials returns 401 Unauthorized")
    void getVideoGameByIdWithWrongAuthTest() {
        // Given
        AllureSteps.logStep(log,
            String.format("Verify game with ID %d exists in database", GAME_ID_FOR_STATUS_AND_BODY_TESTS),
            () -> dbClient.getVideoGameById(GAME_ID_FOR_STATUS_AND_BODY_TESTS).orElseThrow());

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game with wrong credentials",
            () -> apiActions.getByIdWithWrongAuth(GAME_ID_FOR_STATUS_AND_BODY_TESTS, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 401 when wrong credentials are provided")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @TmsLink("XSP-105")
    @KnownIssue("XSP-139: non-existent ID returns 500 instead of 404")
    @Issue("XSP-139")
    @DisplayName("GetVideoGameById – Non-existent ID returns 404 Not Found")
    void getVideoGameByNonExistentIdTest() {
        // Given
        AllureSteps.logStep(log, "Verify game with ID " + NON_EXISTENT_GAME_ID + " does not exist in database",
            () -> commonSteps.verifyGameNotExistsInDatabase(log, NON_EXISTENT_GAME_ID));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game with non-existent ID",
            () -> apiActions.getById(NON_EXISTENT_GAME_ID, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 404 Not Found",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 404 for non-existent game ID %d", NON_EXISTENT_GAME_ID)
                .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @TmsLink("XSP-106")
    @DisplayName("GetVideoGameById – Non-integer path parameter returns 400 Bad Request")
    void getVideoGameByInvalidIdTest() {
        // Given / When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve video game with non-integer ID path parameter",
            () -> apiActions.getByInvalidId(INVALID_GAME_ID, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 400 Bad Request",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 400 when path parameter is non-integer")
                .isEqualTo(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @TmsLink("XSP-138")
    @KnownIssue("XSP-139: non-existent ID returns 500 instead of 404")
    @Issue("XSP-139")
    @DisplayName("GetVideoGameById – Non-existent ID with Accept: application/xml returns XML error response")
    void getVideoGameByNonExistentIdXmlErrorResponseTest() {
        // Given
        AllureSteps.logStep(log, "Verify game with ID " + NON_EXISTENT_GAME_ID + " does not exist in database",
            () -> commonSteps.verifyGameNotExistsInDatabase(log, NON_EXISTENT_GAME_ID));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send GET request to retrieve non-existent video game with Accept: application/xml",
            () -> apiActions.getById(NON_EXISTENT_GAME_ID, ContentType.XML));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 404 Not Found",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 404 for non-existent game ID %d with XML accept",
                    NON_EXISTENT_GAME_ID)
                .isEqualTo(HttpStatus.NOT_FOUND.value()));

        AllureSteps.logStep(log, "Verify response Content-Type is application/xml",
            () -> assertThat(response.getContentType())
                .as("Response Content-Type should contain application/xml")
                .contains(MediaType.APPLICATION_XML_VALUE));

        AllureSteps.logStep(log, "Verify XML error response body matches expected 404 error response",
            () -> {
                ErrorResponseXmlModel errorResponse = XmlUtil.parse(response.asString(), ErrorResponseXmlModel.class);
                SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(errorResponse.getTimestamp())
                        .as("XML error response timestamp should be present")
                        .isNotBlank();
                    softly.assertThat(errorResponse.getStatus())
                        .as("XML error response status should be 404")
                        .isEqualTo(HttpStatus.NOT_FOUND.value());
                    softly.assertThat(errorResponse.getError())
                        .as("XML error response error field should be 'Not Found'")
                        .isEqualTo(HttpStatus.NOT_FOUND.getReasonPhrase());
                    softly.assertThat(errorResponse.getPath())
                        .as("XML error response path should match the requested resource URL")
                        .isEqualTo(getVideoGamePathPrefix() + NON_EXISTENT_GAME_ID);
                });
            });
    }
}
