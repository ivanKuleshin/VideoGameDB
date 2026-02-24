package com.ai.tester.model.api.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "videoGame")
public class VideoGameXmlModel {

    @JacksonXmlProperty(localName = "id")
    private Integer id;

    @JacksonXmlProperty(localName = "name")
    private String name;

    @JacksonXmlProperty(localName = "releaseDate")
    private String releaseDate;

    @JacksonXmlProperty(localName = "reviewScore")
    private Integer reviewScore;

    @JacksonXmlProperty(localName = "category")
    private String category;

    @JacksonXmlProperty(localName = "rating")
    private String rating;
}

