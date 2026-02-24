package com.ai.tester.model.api.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "videoGames")
public class GetAllGamesXmlResponseModel {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "videoGame")
    private List<VideoGameXmlModel> videoGames;
}

