package com.ai.tester.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "videoGames")
@JacksonXmlRootElement(localName = "videoGames")
public class VideoGameList {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "videoGame")
    private List<VideoGame> videoGames;

    public VideoGameList() {
    }

    public VideoGameList(List<VideoGame> videoGames) {
        this.videoGames = videoGames;
    }

    @XmlElement(name = "videoGame")
    public List<VideoGame> getVideoGames() {
        return videoGames;
    }

    public void setVideoGames(List<VideoGame> videoGames) {
        this.videoGames = videoGames;
    }
}

