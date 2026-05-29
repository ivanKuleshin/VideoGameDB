package com.ai.tester.model.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "videoGame")
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
