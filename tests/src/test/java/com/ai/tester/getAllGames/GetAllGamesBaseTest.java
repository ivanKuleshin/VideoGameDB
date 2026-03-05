package com.ai.tester.getAllGames;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;

import java.util.List;

public class GetAllGamesBaseTest extends ApiBaseTest {

    protected List<VideoGameApiModel> prepareExpectedAllGamesResponseList(List<VideoGameDbModel> allVideoGames) {
        return allVideoGames.stream()
            .map(dbModel -> new VideoGameApiModel(
                dbModel.getId(),
                dbModel.getName(),
                dbModel.getReleaseDateAsString(),
                dbModel.getReviewScore(),
                dbModel.getCategory(),
                dbModel.getRating()
            ))
            .toList();
    }

    protected List<VideoGameXmlModel> prepareExpectedAllGamesXmlResponseList(List<VideoGameDbModel> allVideoGames) {
        return allVideoGames.stream()
            .map(dbModel -> {
                VideoGameXmlModel xmlModel = new VideoGameXmlModel();
                xmlModel.setId(dbModel.getId());
                xmlModel.setName(dbModel.getName());
                xmlModel.setReleaseDate(dbModel.getReleaseDateAsString());
                xmlModel.setReviewScore(dbModel.getReviewScore());
                xmlModel.setCategory(dbModel.getCategory());
                xmlModel.setRating(dbModel.getRating());
                return xmlModel;
            })
            .toList();
    }
}
