package com.techietester.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;

@XmlRootElement
@Schema(description = "Represents a video game in the database")
public class VideoGame {

    @Schema(description = "Unique identifier", example = "1")
    private int id;

    @Schema(description = "Title of the video game", example = "The Legend of Zelda")
    private String name;

    @Schema(description = "Release date", example = "1986-02-21")
    private LocalDate releaseDate;

    @Schema(description = "Review score out of 100", example = "95")
    private int reviewScore;

    @Schema(description = "Genre category", example = "Action-Adventure")
    private String category;

    @Schema(description = "Age rating", example = "E")
    private String rating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(int reviewScore) {
        this.reviewScore = reviewScore;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
