package com.ai.tester.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;

@Entity
@Table(name = "VIDEOGAME")
@XmlRootElement
@Schema(description = "Represents a video game in the database")
public class VideoGame {

    @Id
    @Schema(description = "Unique identifier", example = "1")
    private int id;

    @Schema(description = "Title of the video game", example = "The Legend of Zelda")
    private String name;

    @Column(name = "released_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Release date", example = "1986-02-21")
    private LocalDate releaseDate;

    @Column(name = "review_score")
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

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
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
