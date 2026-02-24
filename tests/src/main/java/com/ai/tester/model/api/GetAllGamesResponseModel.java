package com.ai.tester.model.api;

import lombok.Data;

import java.util.List;

@Data
public class GetAllGamesResponseModel {

    private List<VideoGameApiModel> videoGames;
}
