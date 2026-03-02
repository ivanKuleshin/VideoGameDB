package com.ai.tester.data;

import lombok.Getter;

@Getter
public enum Endpoint {

    VIDEOGAMES("/videogames"),
    VIDEOGAME_BY_ID(VIDEOGAMES.path + "/%d"),
    DELETE_EVEN_GAMES(VIDEOGAMES.path + "/delete-even");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }


}
