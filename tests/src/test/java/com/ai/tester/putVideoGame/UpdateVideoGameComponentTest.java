package com.ai.tester.putVideoGame;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.json.UpdateVideoGameRequestModel;
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
@DisplayName("UpdateVideoGame – Check possibility to update an existing video game")
class UpdateVideoGameComponentTest extends UpdateVideoGameBaseTest {

    @Test
    @TmsLinks({@TmsLink("XSP-116"), @TmsLink("XSP-117")})
    @DisplayName("Update video game with valid JSON request")
    void updateVideoGameJsonPositiveTest() {
        // Given
        VideoGameTestDataFixtures jsonInitialFixture = getJsonInitialFixture();
        VideoGameTestDataFixtures jsonUpdateFixture = getJsonUpdateFixture();
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare JSON update request body for " + jsonUpdateFixture.getName(),
            () -> prepareUpdateRequestBody(jsonUpdateFixture));

        try {
            AllureSteps.logStep(log, "Insert initial game into database: " + jsonInitialFixture.getName(),
                () -> dbClient.insertVideoGame(jsonInitialFixture.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request to update video game with JSON body",
                () -> apiActions.put(jsonInitialFixture.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body reflects all updated game fields",
                () -> {
                    VideoGameApiModel actualResponse = response.as(VideoGameApiModel.class);
                    VideoGameApiModel expectedResponse =
                        prepareExpectedApiModel(jsonInitialFixture.getId(), jsonUpdateFixture);
                    assertThat(actualResponse)
                        .as("Response body should reflect all updated game fields")
                        .isEqualTo(expectedResponse);
                });

            VideoGameDbModel updatedGame = commonSteps.verifyGameExistsInDatabase(
                log, jsonInitialFixture.getId(), jsonUpdateFixture.getName());

            AllureSteps.logStep(log, "Verify all updated fields are persisted correctly in database",
                () -> assertThat(updatedGame)
                    .as("Database record should reflect all updated fields")
                    .isEqualTo(prepareExpectedUpdatedDbModel(jsonInitialFixture.getId(), jsonUpdateFixture)));
        } finally {
            dbClient.deleteVideoGameById(jsonInitialFixture.getId());
        }
    }

    @Test
    @TmsLink("XSP-118")
    @DisplayName("Update video game with valid XML request")
    void updateVideoGameXmlPositiveTest() {
        // Given
        VideoGameTestDataFixtures xmlInitialFixture = getXmlInitialFixture();
        VideoGameTestDataFixtures xmlUpdateFixture = getXmlUpdateFixture();
        String xmlBody = AllureSteps.logStepAndReturn(log,
            "Prepare serialized XML update request body for " + xmlUpdateFixture.getName(),
            () -> prepareSerializedXmlBody(xmlUpdateFixture));

        try {
            AllureSteps.logStep(log, "Insert initial game into database: " + xmlInitialFixture.getName(),
                () -> dbClient.insertVideoGame(xmlInitialFixture.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request to update video game with XML body",
                () -> apiActions.put(xmlInitialFixture.getId(), xmlBody, ContentType.XML));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify XML response body reflects all updated game fields",
                () -> {
                    VideoGameApiModel actualXmlResponse =
                        XmlUtil.parse(response.asString(), VideoGameApiModel.class);
                    VideoGameApiModel expectedXmlResponse =
                        prepareExpectedApiModel(xmlInitialFixture.getId(), xmlUpdateFixture);
                    assertThat(actualXmlResponse)
                        .as("XML response body should reflect all updated game fields")
                        .isEqualTo(expectedXmlResponse);
                });

            VideoGameDbModel updatedGame = commonSteps.verifyGameExistsInDatabase(
                log, xmlInitialFixture.getId(), xmlUpdateFixture.getName());

            AllureSteps.logStep(log, "Verify all updated fields are persisted correctly in database",
                () -> assertThat(updatedGame)
                    .as("Database record should reflect all updated fields")
                    .isEqualTo(prepareExpectedUpdatedDbModel(xmlInitialFixture.getId(), xmlUpdateFixture)));
        } finally {
            dbClient.deleteVideoGameById(xmlInitialFixture.getId());
        }
    }

    @Test
    @TmsLink("XSP-119")
    @DisplayName("Update video game when path parameter ID differs from request body ID")
    @Disabled("XSP-119: KNOWN BUG — app updates record with body ID instead of path param ID — enable after app fix")
    void updateVideoGamePathParamDrivesUpdateTest() {
        // Given
        VideoGameTestDataFixtures pathPrimaryFixture = getPathPrimaryFixture();
        VideoGameTestDataFixtures pathSecondaryFixture = getPathSecondaryFixture();
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update body containing secondary game ID " + pathSecondaryFixture.getId(),
            () -> prepareUpdateRequestBody(pathSecondaryFixture));

        try {
            AllureSteps.logStep(log, "Insert primary and secondary test games into database",
                () -> {
                    dbClient.insertVideoGame(pathPrimaryFixture.getGameData());
                    dbClient.insertVideoGame(pathSecondaryFixture.getGameData());
                });

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request to primary game path with body containing secondary game ID",
                () -> apiActions.put(pathPrimaryFixture.getId(), updateBody, ContentType.JSON));

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
                            pathPrimaryFixture.getId(), pathSecondaryFixture.getId())
                        .isEqualTo(pathPrimaryFixture.getId());
                });
        } finally {
            dbClient.deleteVideoGameById(pathPrimaryFixture.getId());
            dbClient.deleteVideoGameById(pathSecondaryFixture.getId());
        }
    }

    @Test
    @TmsLink("XSP-120")
    @DisplayName("Update video game without authentication credentials")
    void updateVideoGameMissingCredentialsReturns401Test() {
        // Given
        VideoGameTestDataFixtures missingAuthFixture = getMissingAuthFixture();
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body",
            () -> prepareUpdateRequestBody(missingAuthFixture));

        try {
            AllureSteps.logStep(log, "Insert test game into database: " + missingAuthFixture.getName(),
                () -> dbClient.insertVideoGame(missingAuthFixture.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request without authentication credentials",
                () -> apiActions.putWithoutAuth(missingAuthFixture.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 401 Unauthorized when credentials are missing")
                    .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

            commonSteps.verifyGameExistsInDatabase(
                log, missingAuthFixture.getId(), missingAuthFixture.getName());
        } finally {
            dbClient.deleteVideoGameById(missingAuthFixture.getId());
        }
    }

    @Test
    @TmsLink("XSP-121")
    @DisplayName("Update video game with invalid authentication credentials")
    void updateVideoGameInvalidCredentialsReturns401Test() {
        // Given
        VideoGameTestDataFixtures wrongAuthFixture = getWrongAuthFixture();
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body",
            () -> prepareUpdateRequestBody(wrongAuthFixture));

        try {
            AllureSteps.logStep(log, "Insert test game into database: " + wrongAuthFixture.getName(),
                () -> dbClient.insertVideoGame(wrongAuthFixture.getGameData()));

            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send PUT request with wrong authentication credentials",
                () -> apiActions.putWithWrongAuth(wrongAuthFixture.getId(), updateBody, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 401 Unauthorized when credentials are invalid")
                    .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

            commonSteps.verifyGameExistsInDatabase(
                log, wrongAuthFixture.getId(), wrongAuthFixture.getName());
        } finally {
            dbClient.deleteVideoGameById(wrongAuthFixture.getId());
        }
    }

    @Test
    @TmsLink("XSP-122")
    @DisplayName("Update non-existent video game")
    @Disabled("XSP-122: KNOWN BUG — app returns 500 for non-existent ID instead of 404 — enable after app fix")
    void updateVideoGameNonExistentIdReturns404Test() {
        // Given
        VideoGameTestDataFixtures jsonUpdateFixture = getJsonUpdateFixture();
        commonSteps.verifyGameNotExistsInDatabase(log, NON_EXISTING_GAME_ID);

        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body for non-existent game",
            () -> prepareUpdateRequestBody(jsonUpdateFixture));

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
        VideoGameTestDataFixtures jsonUpdateFixture = getJsonUpdateFixture();
        UpdateVideoGameRequestModel updateBody = AllureSteps.logStepAndReturn(log,
            "Prepare update request body",
            () -> prepareUpdateRequestBody(jsonUpdateFixture));

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

