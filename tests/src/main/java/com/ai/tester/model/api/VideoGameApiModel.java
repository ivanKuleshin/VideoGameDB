package com.ai.tester.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoGameApiModel {

    private Integer id;
    private String name;

    @JsonProperty("releaseDate")
    private String releaseDate;

    @JsonProperty("reviewScore")
    private Integer reviewScore;

    private String category;
    private String rating;
}
