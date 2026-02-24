package com.ai.tester.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class VideoGameDbModel {

    private Integer id;
    private String name;

    @JsonProperty("RELEASED_ON")
    private String releaseDate;

    @JsonProperty("REVIEW_SCORE")
    private Integer reviewScore;

    private String category;
    private String rating;
}
