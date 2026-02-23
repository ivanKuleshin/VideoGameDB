package com.techietester.resource;

import com.techietester.base.BaseApiTest;
import com.techietester.builder.VideoGameBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Component (black-box) tests for {@code VideoGameResource}.
 *
 * <p>The full Spring Boot application is started on a random port before each
 * test class. No mocks are used. HTTP Basic auth is provided by the shared
 * {@link BaseApiTest#spec}.
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

    private static final String VIDEOGAMES_PATH   = "/videogames";
    private static final int    SEEDED_GAME_ID    = 1;
    private static final String SEEDED_GAME_NAME  = "Resident Evil 4";
    private static final int    TEST_GAME_ID      = 100;

    // ── Test-data setup ───────────────────────────────────────────────────────

    /**
     * Ensure a game with ID=100 exists before every test that needs a
     * writable target. If it already exists (e.g. from a previous test) the
     * duplicate INSERT is silently ignored by catching the 5xx; the game is
     * re-inserted via delete-then-post to guarantee a known state.
     */
    @BeforeEach
    void ensureTestGameExists() {
        // Delete first (idempotent – 200 whether it existed or not, resource
        // returns 200 even for non-existent IDs)
        given(spec)
                .delete(VIDEOGAMES_PATH + "/" + TEST_GAME_ID);

        // Re-create with known default values from the builder
        Map<String, Object> body = new VideoGameBuilder().build();   // id=100 by default
        given(spec)
                .body(body)
                .post(VIDEOGAMES_PATH)
                .then()
                .statusCode(200);
    }

    // ── GET /videogames ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /videogames – returns 200 and a non-empty list of video games")
    void getAllVideoGames_returns200AndNonEmptyList() {
        given(spec)
                .get(VIDEOGAMES_PATH)
        .then()
                .statusCode(200)
                .body("videoGame",        notNullValue())
                .body("videoGame.size()", greaterThanOrEqualTo(10));
    }

    // ── GET /videogames/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /videogames/{id} – returns 200 and correct game fields for a seeded record")
    void getVideoGameById_returns200AndCorrectGame() {
        given(spec)
                .pathParam("id", SEEDED_GAME_ID)
                .get(VIDEOGAMES_PATH + "/{id}")
        .then()
                .statusCode(200)
                .body("id",          equalTo(SEEDED_GAME_ID))
                .body("name",        equalTo(SEEDED_GAME_NAME))
                .body("reviewScore", equalTo(85))
                .body("category",    equalTo("Shooter"))
                .body("rating",      equalTo("Universal"))
                .body("releaseDate", notNullValue());
    }

    // ── POST /videogames ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /videogames – returns 200 and success message when game is created")
    void createVideoGame_returns200AndSuccessMessage() {
        // Use a unique ID different from the one maintained by @BeforeEach
        Map<String, Object> body = new VideoGameBuilder()
                .withId(101)
                .withName("New Test Game")
                .withReviewScore(92)
                .withCategory("RPG")
                .withRating("PG-13")
                .build();

        given(spec)
                .body(body)
                .post(VIDEOGAMES_PATH)
        .then()
                .statusCode(200)
                .body("status", equalTo("Record Added Successfully"));

        // Verify the game actually exists in the DB by fetching it back
        given(spec)
                .pathParam("id", 101)
                .get(VIDEOGAMES_PATH + "/{id}")
        .then()
                .statusCode(200)
                .body("name",        equalTo("New Test Game"))
                .body("reviewScore", equalTo(92));

        // Cleanup so this test is side-effect free for others
        given(spec).delete(VIDEOGAMES_PATH + "/101");
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

        given(spec)
                .pathParam("id", TEST_GAME_ID)
                .body(updatedBody)
                .put(VIDEOGAMES_PATH + "/{id}")
        .then()
                .statusCode(200)
                .body("id",          equalTo(TEST_GAME_ID))
                .body("name",        equalTo("Updated Game Title"))
                .body("reviewScore", equalTo(99))
                .body("category",    equalTo("Strategy"))
                .body("rating",      equalTo("Mature"));
    }

    // ── DELETE /videogames/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /videogames/{id} – returns 200 and success message when game is deleted")
    void deleteVideoGame_returns200AndSuccessMessage() {
        given(spec)
                .pathParam("id", TEST_GAME_ID)
                .delete(VIDEOGAMES_PATH + "/{id}")
        .then()
                .statusCode(200)
                .body("status", equalTo("Record Deleted Successfully"));

        // Verify the game is gone – the resource throws NoSuchElementException
        // which Jersey maps to a 500; assert it is no longer reachable as 200
        given(spec)
                .pathParam("id", TEST_GAME_ID)
                .get(VIDEOGAMES_PATH + "/{id}")
        .then()
                .statusCode(not(200));
    }

    // ── DELETE /videogames/delete-even ────────────────────────────────────────

    @Test
    @DisplayName("DELETE /videogames/delete-even – returns 200 and reports number of records deleted")
    void deleteEvenVideoGames_returns200AndDeletedCount() {
        given(spec)
                .delete(VIDEOGAMES_PATH + "/delete-even")
        .then()
                .statusCode(200)
                .body("status", containsString("Deleted"))
                .body("status", containsString("records with even IDs"));
    }
}

