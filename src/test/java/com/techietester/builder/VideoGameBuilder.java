package com.techietester.builder;

import java.util.HashMap;
import java.util.Map;

/**
 * Fluent test-data builder for a VideoGame JSON request body.
 *
 * <p>Provides sensible defaults so every test only overrides the fields it
 * actually cares about.  Returns a plain {@link Map} so REST Assured can
 * serialise it to JSON without requiring the production model on the test
 * classpath.
 *
 * <p>Default values:
 * <ul>
 *   <li>id            = 100</li>
 *   <li>name          = "Test Game"</li>
 *   <li>releaseDate   = "2020-01-01"</li>
 *   <li>reviewScore   = 80</li>
 *   <li>category      = "Action"</li>
 *   <li>rating        = "Universal"</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>{@code
 *   Map<String, Object> body = new VideoGameBuilder()
 *       .withId(200)
 *       .withName("My Game")
 *       .build();
 * }</pre>
 */
public class VideoGameBuilder {

    private int    id          = 100;
    private String name        = "Test Game";
    private String releaseDate = "2020-01-01";
    private int    reviewScore = 80;
    private String category    = "Action";
    private String rating      = "Universal";

    public VideoGameBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public VideoGameBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public VideoGameBuilder withReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public VideoGameBuilder withReviewScore(int reviewScore) {
        this.reviewScore = reviewScore;
        return this;
    }

    public VideoGameBuilder withCategory(String category) {
        this.category = category;
        return this;
    }

    public VideoGameBuilder withRating(String rating) {
        this.rating = rating;
        return this;
    }

    /**
     * Builds and returns the video game as a {@link Map} ready to be
     * serialised to JSON by REST Assured.
     */
    public Map<String, Object> build() {
        Map<String, Object> body = new HashMap<>();
        body.put("id",          id);
        body.put("name",        name);
        body.put("releaseDate", releaseDate);
        body.put("reviewScore", reviewScore);
        body.put("category",    category);
        body.put("rating",      rating);
        return body;
    }
}

