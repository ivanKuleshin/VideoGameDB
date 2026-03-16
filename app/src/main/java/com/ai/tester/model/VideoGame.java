package com.ai.tester.model;

import com.ai.tester.model.adapter.LocalDateAdapter;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "VIDEOGAME")
@XmlRootElement
@Schema(description = "Represents a video game in the database")
@Data
public class VideoGame {

    @Id
    @Schema(description = "Unique identifier", example = "1")
    private int id;

    @Schema(description = "Title of the video game", example = "The Legend of Zelda")
    private String name;

    @Column(name = "released_on")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    @Schema(description = "Release date", example = "1986-02-21")
    private LocalDate releaseDate;

    @Column(name = "review_score")
    @Schema(description = "Review score out of 100", example = "95")
    private int reviewScore;

    @Schema(description = "Genre category", example = "Action-Adventure")
    private String category;

    @Schema(description = "Age rating", example = "E")
    private String rating;
}
