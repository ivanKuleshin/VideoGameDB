package com.ai.tester.model.api.json;

import lombok.Data;

import java.util.List;

@Data
public class GetAllGamesResponseModel {

    private List<VideoGameApiModel> videoGames;
}
