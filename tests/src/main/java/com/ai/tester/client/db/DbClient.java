package com.ai.tester.client.db;

import com.ai.tester.model.db.VideoGameDbModel;

import java.util.List;
import java.util.Optional;

public interface DbClient {

    List<VideoGameDbModel> getAllVideoGames();

    Optional<VideoGameDbModel> getVideoGameById(int id);

    void insertVideoGame(VideoGameDbModel videoGame);

    void deleteVideoGameById(int id);
}

