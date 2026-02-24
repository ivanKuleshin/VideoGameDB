package com.ai.tester.model.db;

import com.ai.tester.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VideoGameDbModel {

    private Integer id;
    private String name;

    @JsonProperty("RELEASED_ON")
    private Long releaseDate;

    @JsonProperty("REVIEW_SCORE")
    private Integer reviewScore;

    private String category;
    private String rating;

    public String getReleaseDateAsString() {
        return DateUtil.epochMillisToDateString(releaseDate);
    }
}
