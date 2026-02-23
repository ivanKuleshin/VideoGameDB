package com.ai.tester.getAllGames;

import com.ai.tester.BaseApiTest;
import com.ai.tester.builder.VideoGameBuilder;
import com.ai.tester.client.HttpClient;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Component (black-box) tests for {@code VideoGameResource}.
 *
 * <p>The full Spring Boot application is started on a random port before each
 * test class. No mocks are used. All HTTP calls are made through
 * {@link HttpClient} which is configured in
 * {@link BaseApiTest}.
 *
 * <p><b>Test-data strategy:</b> The H2 database is seeded once by
 * {@code schema.sql} when the Spring context starts. Seeded IDs are 1-10.
 * Tests that create/update/delete data use IDs &gt;= 100 (well outside the
 * seeded range) so they never interfere with the read-only happy-path tests.
 *
 * <p>A fresh record with ID=100 is created in {@code @BeforeEach} so that the
 * update and delete tests always have a stable target regardless of execution
 * order.
 */
@DisplayName("VideoGameResource – Happy-Path Component Tests")
class VideoGameResourceTest extends BaseApiTest {

    // ── Constants ─────────────────────────────────────────────────────────────

    private static final String VIDEOGAMES_PATH = "/videogames";
    private static final int SEEDED_GAME_ID = 1;
    private static final String SEEDED_GAME_NAME = "Resident Evil 4";
    private static final int TEST_GAME_ID = 100;

    // ── Test-data setup ───────────────────────────────────────────────────────

    /**
     * Ensures a game with ID=100 exists before every test that needs a
     * writable target.  Uses delete-then-post to guarantee a clean known state
     * regardless of execution order.
     */
    @BeforeEach
    void ensureTestGameExists() {
        httpClient.delete(VIDEOGAMES_PATH + "/" + TEST_GAME_ID, ContentType.JSON);

        Map<String, Object> body = new VideoGameBuilder().build();
        httpClient.post(VIDEOGAMES_PATH, body, ContentType.JSON)
            .then()
            .statusCode(200);
    }

    // ── GET /videogames ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /videogames – returns 200 and a non-empty list of video games")
    void getAllVideoGames_returns200AndNonEmptyList() {
        httpClient.get(VIDEOGAMES_PATH, ContentType.JSON)
            .then()
            .statusCode(200)
            .body("videoGame", notNullValue())
            .body("videoGame.size()", greaterThanOrEqualTo(1));
    }

    // ── GET /videogames/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /videogames/{id} – returns 200 and correct game fields for a seeded record")
    void getVideoGameById_returns200AndCorrectGame() {
        httpClient.get(VIDEOGAMES_PATH + "/" + SEEDED_GAME_ID, ContentType.JSON)
            .then()
            .statusCode(200)
            .body("id", equalTo(SEEDED_GAME_ID))
            .body("name", equalTo(SEEDED_GAME_NAME))
            .body("reviewScore", equalTo(85))
            .body("category", equalTo("Shooter"))
            .body("rating", equalTo("Universal"))
            .body("releaseDate", notNullValue());
    }

    // ── POST /videogames ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /videogames – returns 200 and success message when game is created")
    void createVideoGame_returns200AndSuccessMessage() {
        Map<String, Object> body = new VideoGameBuilder()
            .withId(101)
            .withName("New Test Game")
            .withReviewScore(92)
            .withCategory("RPG")
            .withRating("PG-13")
            .build();

        httpClient.post(VIDEOGAMES_PATH, body, ContentType.JSON)
            .then()
            .statusCode(200)
            .body("status", equalTo("Record Added Successfully"));

        httpClient.get(VIDEOGAMES_PATH + "/101", ContentType.JSON)
            .then()
            .statusCode(200)
            .body("name", equalTo("New Test Game"))
            .body("reviewScore", equalTo(92));

        httpClient.delete(VIDEOGAMES_PATH + "/101", ContentType.JSON);
    }

    // ── PUT /videogames/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /videogames/{id} – returns 200 and the updated game fields")
    void updateVideoGame_returns200AndUpdatedFields() {
        Map<String, Object> updatedBody = new VideoGameBuilder()
            .withId(TEST_GAME_ID)
            .withName("Updated Game Title")
            .withReviewScore(99)
            .withCategory("Strategy")
            .withRating("Mature")
            .build();

        httpClient.put(VIDEOGAMES_PATH + "/" + TEST_GAME_ID, updatedBody, ContentType.JSON)
            .then()
            .statusCode(200)
            .body("id", equalTo(TEST_GAME_ID))
            .body("name", equalTo("Updated Game Title"))
            .body("reviewScore", equalTo(99))
            .body("category", equalTo("Strategy"))
            .body("rating", equalTo("Mature"));
    }

    // ── DELETE /videogames/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /videogames/{id} – returns 200 and success message when game is deleted")
    void deleteVideoGame_returns200AndSuccessMessage() {
        httpClient.delete(VIDEOGAMES_PATH + "/" + TEST_GAME_ID, ContentType.JSON)
            .then()
            .statusCode(200)
            .body("status", equalTo("Record Deleted Successfully"));

        httpClient.get(VIDEOGAMES_PATH + "/" + TEST_GAME_ID, ContentType.JSON)
            .then()
            .statusCode(not(200));
    }

    // ── DELETE /videogames/delete-even ────────────────────────────────────────

    @Test
    @DisplayName("DELETE /videogames/delete-even – returns 200 and reports number of records deleted")
    void deleteEvenVideoGames_returns200AndDeletedCount() {
        httpClient.delete(VIDEOGAMES_PATH + "/delete-even", ContentType.JSON)
            .then()
            .statusCode(200)
            .body("status", containsString("Deleted"))
            .body("status", containsString("records with even IDs"));
    }
}
