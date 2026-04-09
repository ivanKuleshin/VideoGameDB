package com.ai.tester.putVideoGame;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.json.UpdateVideoGameRequestModel;
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
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
@DisplayName("UpdateVideoGame – Check possibility to update an existing video game")
class UpdateVideoGameComponentTest extends UpdateVideoGameBaseTest {

    @Test
    @TmsLinks({@TmsLink("XSP-116"), @TmsLink("XSP-117")})
    @DisplayName("Update video game with valid JSON request")
    void updateVideoGameJsonPositiveTest() {
        // Given
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare JSON update request body",
            () -> prepareUpdateRequestBody(JSON_UPDATE_FIXTURE));

        try {
            AllureSteps.logStep(log, "Insert initial JSON test game into database",
                () -> dbClient.insertVideoGame(JSON_INITIAL_FIXTURE.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request to update video game with JSON body",
                () -> apiActions.put(JSON_INITIAL_FIXTURE.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body reflects all updated game fields",
                () -> {
                    VideoGameApiModel actualResponse = response.as(VideoGameApiModel.class);
                    VideoGameApiModel expectedResponse =
                        prepareExpectedApiModel(JSON_INITIAL_FIXTURE.getId(), JSON_UPDATE_FIXTURE);
                    assertThat(actualResponse)
                        .as("Response body should reflect all updated game fields")
                        .isEqualTo(expectedResponse);
                });

            VideoGameDbModel updatedGame = commonSteps.verifyGameExistsInDatabase(
                log, JSON_INITIAL_FIXTURE.getId(), JSON_UPDATE_FIXTURE.getName());

            AllureSteps.logStep(log, "Verify all updated fields are persisted correctly in database",
                () -> assertThat(updatedGame)
                    .as("Database record should reflect all updated fields")
                    .isEqualTo(prepareExpectedUpdatedDbModel(JSON_INITIAL_FIXTURE.getId(), JSON_UPDATE_FIXTURE)));
        } finally {
            dbClient.deleteVideoGameById(JSON_INITIAL_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-118")
    @DisplayName("Update video game with valid XML request")
    void updateVideoGameXmlPositiveTest() {
        // Given
        String xmlBody = AllureSteps.logStepAndReturn(log,
            "Prepare serialized XML update request body",
            () -> prepareSerializedXmlBody(XML_UPDATE_FIXTURE));

        try {
            AllureSteps.logStep(log, "Insert initial XML test game into database",
                () -> dbClient.insertVideoGame(XML_INITIAL_FIXTURE.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request to update video game with XML body",
                () -> apiActions.put(XML_INITIAL_FIXTURE.getId(), xmlBody, ContentType.XML));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify XML response body reflects all updated game fields",
                () -> {
                    VideoGameXmlModel actualXmlResponse =
                        XmlUtil.parse(response.asString(), VideoGameXmlModel.class);
                    VideoGameXmlModel expectedXmlResponse =
                        prepareExpectedXmlModel(XML_INITIAL_FIXTURE.getId(), XML_UPDATE_FIXTURE);
                    assertThat(actualXmlResponse)
                        .as("XML response body should reflect all updated game fields")
                        .isEqualTo(expectedXmlResponse);
                });

            VideoGameDbModel updatedGame = commonSteps.verifyGameExistsInDatabase(
                log, XML_INITIAL_FIXTURE.getId(), XML_UPDATE_FIXTURE.getName());

            AllureSteps.logStep(log, "Verify all updated fields are persisted correctly in database",
                () -> assertThat(updatedGame)
                    .as("Database record should reflect all updated fields")
                    .isEqualTo(prepareExpectedUpdatedDbModel(XML_INITIAL_FIXTURE.getId(), XML_UPDATE_FIXTURE)));
        } finally {
            dbClient.deleteVideoGameById(XML_INITIAL_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-119")
    @DisplayName("Update video game when path parameter ID differs from request body ID")
    void updateVideoGamePathParamDrivesUpdateTest() {
        // Given
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update body for secondary fixture",
            () -> prepareUpdateRequestBody(PATH_SECONDARY_FIXTURE));

        try {
            AllureSteps.logStep(log, "Insert primary and secondary test games into database",
                () -> {
                    dbClient.insertVideoGame(PATH_PRIMARY_FIXTURE.getGameData());
                    dbClient.insertVideoGame(PATH_SECONDARY_FIXTURE.getGameData());
                });

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request to primary game path with body containing secondary game ID",
                () -> apiActions.put(PATH_PRIMARY_FIXTURE.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response contains path param ID, not body ID",
                () -> {
                    VideoGameApiModel actualResponse = response.as(VideoGameApiModel.class);
                    assertThat(actualResponse.getId())
                        .as("Response game ID should equal path param ID %d, not body ID %d",
                            PATH_PRIMARY_FIXTURE.getId(), PATH_SECONDARY_FIXTURE.getId())
                        .isEqualTo(PATH_PRIMARY_FIXTURE.getId());
                });
        } finally {
            dbClient.deleteVideoGameById(PATH_PRIMARY_FIXTURE.getId());
            dbClient.deleteVideoGameById(PATH_SECONDARY_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-120")
    @DisplayName("Update video game without authentication credentials")
    void updateVideoGameMissingCredentialsReturns401Test() {
        // Given
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body",
            () -> prepareUpdateRequestBody(MISSING_AUTH_FIXTURE));

        try {
            AllureSteps.logStep(log, "Insert test game into database",
                () -> dbClient.insertVideoGame(MISSING_AUTH_FIXTURE.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request without authentication credentials",
                () -> apiActions.putWithoutAuth(MISSING_AUTH_FIXTURE.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 401 Unauthorized when credentials are missing")
                    .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

            commonSteps.verifyGameExistsInDatabase(
                log, MISSING_AUTH_FIXTURE.getId(), MISSING_AUTH_FIXTURE.getName());
        } finally {
            dbClient.deleteVideoGameById(MISSING_AUTH_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-121")
    @DisplayName("Update video game with invalid authentication credentials")
    void updateVideoGameInvalidCredentialsReturns401Test() {
        // Given
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body",
            () -> prepareUpdateRequestBody(WRONG_AUTH_FIXTURE));

        try {
            AllureSteps.logStep(log, "Insert test game into database",
                () -> dbClient.insertVideoGame(WRONG_AUTH_FIXTURE.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request with wrong authentication credentials",
                () -> apiActions.putWithWrongAuth(WRONG_AUTH_FIXTURE.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 401 Unauthorized when credentials are invalid")
                    .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

            commonSteps.verifyGameExistsInDatabase(
                log, WRONG_AUTH_FIXTURE.getId(), WRONG_AUTH_FIXTURE.getName());
        } finally {
            dbClient.deleteVideoGameById(WRONG_AUTH_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-122")
    @DisplayName("Update non-existent video game")
    void updateVideoGameNonExistentIdReturns404Test() {
        // Given
        commonSteps.verifyGameNotExistsInDatabase(log, NON_EXISTING_GAME_ID);

        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body for non-existent game",
            () -> prepareUpdateRequestBody(JSON_UPDATE_FIXTURE));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send PUT request for non-existent video game ID",
            () -> apiActions.put(NON_EXISTING_GAME_ID, updateBody, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 404 Not Found",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 404 Not Found for non-existent game ID")
                .isEqualTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    @TmsLink("XSP-123")
    @DisplayName("Update video game with non-integer path parameter")
    void updateVideoGameNonIntegerIdReturns404Or400Test() {
        // Given
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body",
            () -> prepareUpdateRequestBody(JSON_UPDATE_FIXTURE));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send PUT request with non-integer path parameter",
            () -> apiActions.putByInvalidId(NON_INTEGER_GAME_ID, updateBody, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 400 or 404",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 400 or 404 for non-integer path parameter")
                .isIn(HttpStatus.BAD_REQUEST.value(), HttpStatus.NOT_FOUND.value()));
    }
}

