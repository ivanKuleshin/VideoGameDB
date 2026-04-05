package com.ai.tester.model.api.json;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateVideoGameRequestModel {

    private Integer id;
    private String name;
    private String releaseDate;
    private Integer reviewScore;
    private String category;
    private String rating;
}

