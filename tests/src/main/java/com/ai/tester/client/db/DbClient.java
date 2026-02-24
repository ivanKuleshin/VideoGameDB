package com.ai.tester.client.db;

import com.ai.tester.model.VideoGameDbModel;

import java.util.List;
import java.util.Optional;

public interface DbClient {

    List<VideoGameDbModel> getAllVideoGames();

    Optional<VideoGameDbModel> getVideoGameById(int id);
}

