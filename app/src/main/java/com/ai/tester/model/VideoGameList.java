package com.ai.tester.model;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name = "videoGames")
public class VideoGameList {

    private List<VideoGame> videoGames;

    public VideoGameList() {
    }

    public VideoGameList(List<VideoGame> videoGames) {
        this.videoGames = videoGames;
    }

    @XmlElement(name = "videoGames")
    public List<VideoGame> getVideoGames() {
        return videoGames;
    }

    public void setVideoGames(List<VideoGame> videoGames) {
        this.videoGames = videoGames;
    }
}

