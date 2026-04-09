package com.ai.tester.postVideoGame;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.model.api.json.PostVideoGameRequestModel;
import com.ai.tester.model.api.json.PostVideoGameResponseModel;
import com.ai.tester.model.api.xml.PostVideoGameXmlRequestModel;
import com.ai.tester.model.api.xml.PostVideoGameXmlResponseModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.XmlUtil;
import com.ai.tester.annotation.KnownIssue;
import io.qameta.allure.Issue;
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
@DisplayName("PostVideoGame – Check possibility to create a new video game")
class PostVideoGameComponentTest extends PostVideoGameBaseTest {

    @Test
    @TmsLinks({@TmsLink("XSP-108"), @TmsLink("XSP-109")})
    @DisplayName("Create video game with valid JSON request")
    void postVideoGameWithJsonPositiveTest() {
        // Given
        PostVideoGameRequestModel videoGameRequest = AllureSteps.logStepAndReturn(log,
            "Prepare JSON request body for primary fixture",
            () -> prepareVideoGameRequest(JSON_DB_FIXTURE));

        try {
            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send POST request to create a new video game",
                () -> apiActions.post(videoGameRequest, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body contains status 'Record Added Successfully'",
                () -> {
                    PostVideoGameResponseModel actualResponse = response.as(PostVideoGameResponseModel.class);
                    assertThat(actualResponse)
                        .as("Response body should match the expected status")
                        .isEqualTo(prepareExpectedPostJsonResponse());
                });

            VideoGameDbModel savedGame = commonSteps.verifyGameExistsInDatabase(
                log, JSON_DB_FIXTURE.getId(), videoGameRequest.getName());

            AllureSteps.logStep(log, "Verify all fields match expected values",
                () -> assertThat(savedGame)
                    .as("Saved game should match all fields from the posted fixture")
                    .isEqualTo(prepareExpectedGameDbModel(videoGameRequest)));
        } finally {
            dbClient.deleteVideoGameById(JSON_DB_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-110")
    @DisplayName("Create video game with valid XML request")
    void postVideoGameWithXmlPositiveTest() {
        // Given
        PostVideoGameXmlRequestModel xmlRequest = AllureSteps.logStepAndReturn(log,
            "Prepare XML request body for XML fixture",
            () -> prepareXmlVideoGameRequest(XML_FIXTURE));

        try {
            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send POST request with XML content type to create a new video game",
                () -> apiActions.post(serializeXmlRequest(xmlRequest), ContentType.XML));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body contains status 'Record Added Successfully'",
                () -> {
                    PostVideoGameXmlResponseModel actualXmlResponse =
                        XmlUtil.parse(response.asString(), PostVideoGameXmlResponseModel.class);
                    assertThat(actualXmlResponse)
                        .as("XML response body should match the expected status")
                        .isEqualTo(prepareExpectedPostXmlResponse());
                });

            VideoGameDbModel savedGame = commonSteps.verifyGameExistsInDatabase(
                log, XML_FIXTURE.getId(), xmlRequest.getName());

            AllureSteps.logStep(log, "Verify all fields match expected values",
                () -> assertThat(savedGame)
                    .as("Saved game should match all fields from the XML request")
                    .isEqualTo(prepareExpectedGameDbModel(xmlRequest)));
        } finally {
            dbClient.deleteVideoGameById(XML_FIXTURE.getId());
        }
    }

    @Test
    @TmsLink("XSP-111")
    @DisplayName("Create video game without authentication")
    void postVideoGameWithMissingCredentialsTest() {
        // Given
        PostVideoGameRequestModel videoGameRequest = AllureSteps.logStepAndReturn(log,
            "Prepare request body for primary fixture",
            () -> prepareVideoGameRequest(JSON_DB_FIXTURE));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send POST request without authentication credentials",
            () -> apiActions.postWithoutAuth(videoGameRequest, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 401 Unauthorized when credentials are missing")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        commonSteps.verifyGameNotExistsInDatabase(log, JSON_DB_FIXTURE.getId());
    }

    @Test
    @TmsLink("XSP-112")
    @DisplayName("Create video game with wrong credentials")
    void postVideoGameWithInvalidCredentialsTest() {
        // Given
        PostVideoGameRequestModel videoGameRequest = AllureSteps.logStepAndReturn(log,
            "Prepare request body for primary fixture",
            () -> prepareVideoGameRequest(JSON_DB_FIXTURE));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send POST request with wrong authentication credentials",
            () -> apiActions.postWithWrongAuth(videoGameRequest, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 401 Unauthorized",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 401 Unauthorized when credentials are wrong")
                .isEqualTo(HttpStatus.UNAUTHORIZED.value()));

        commonSteps.verifyGameNotExistsInDatabase(log, JSON_DB_FIXTURE.getId());
    }

    @Test
    @TmsLink("XSP-113")
    @KnownIssue("XSP-113: app has no duplicate-ID guard — returns 500 instead of 409")
    @Issue("XSP-113")
    @DisplayName("Create video game with duplicate ID")
    void postVideoGameWithDuplicateIdTest() {
        // Given
        commonSteps.verifyGameExistsInDatabase(log, DUPLICATE_GAME_FIXTURE.getId(), DUPLICATE_GAME_FIXTURE.getName());

        PostVideoGameRequestModel videoGameRequest = AllureSteps.logStepAndReturn(log,
            "Prepare JSON request body with duplicate game ID",
            () -> prepareVideoGameRequest(DUPLICATE_GAME_FIXTURE));

        // When
        Response response = AllureSteps.logStepAndReturn(log,
            "Send POST request with duplicate game ID",
            () -> apiActions.post(videoGameRequest, ContentType.JSON));

        // Then
        AllureSteps.logStep(log, "Verify response status code is 500 Internal Server Error",
            () -> assertThat(response.getStatusCode())
                .as("Response status code should be 500 for duplicate ID")
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    @TmsLink("XSP-114")
    @DisplayName("Create video game with only id field")
    void postVideoGameWithIdOnlyFieldTest() {
        // Given
        PostVideoGameRequestModel videoGameRequest = AllureSteps.logStepAndReturn(log,
            "Prepare request body containing only the id field",
            () -> prepareIdOnlyVideoGameRequest(ID_ONLY_FIXTURE));

        try {
            // When
            Response response = AllureSteps.logStepAndReturn(log,
                "Send POST request with id-only body",
                () -> apiActions.post(videoGameRequest, ContentType.JSON));

            // Then
            AllureSteps.logStep(log, "Verify response status code is 200",
                () -> assertThat(response.getStatusCode())
                    .as("Response status code should be 200 OK")
                    .isEqualTo(HttpStatus.OK.value()));

            AllureSteps.logStep(log, "Verify response body contains status 'Record Added Successfully'",
                () -> {
                    PostVideoGameResponseModel actualResponse = response.as(PostVideoGameResponseModel.class);
                    assertThat(actualResponse)
                        .as("Response body should match the expected status")
                        .isEqualTo(prepareExpectedPostJsonResponse());
                });

            VideoGameDbModel savedGame = commonSteps.verifyGameExistsInDatabase(
                log, ID_ONLY_FIXTURE.getId());

            AllureSteps.logStep(log, "Verify all fields match expected values",
                () -> assertThat(savedGame)
                    .as("Saved game should have empty name and null optional fields")
                    .isEqualTo(prepareExpectedIdOnlyDbModel(videoGameRequest)));
        } finally {
            dbClient.deleteVideoGameById(ID_ONLY_FIXTURE.getId());
        }
    }
}
