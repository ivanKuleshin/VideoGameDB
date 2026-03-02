package com.ai.tester.getVideoGameById;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;

import java.util.List;

public class GetVideoGameByIdBaseTest extends ApiBaseTest {

    protected VideoGameDbModel getFirstVideoGameFromDatabase() {
        List<VideoGameDbModel> allGames = dbClient.getAllVideoGames();
        if (allGames.isEmpty()) {
            throw new RuntimeException("Database should contain at least one video game");
        }
        return allGames.getFirst();
    }

    protected VideoGameApiModel prepareExpectedVideoGameResponse(VideoGameDbModel dbModel) {
        return new VideoGameApiModel(
            dbModel.getId(),
            dbModel.getName(),
            dbModel.getReleaseDateAsString(),
            dbModel.getReviewScore(),
            dbModel.getCategory(),
            dbModel.getRating()
        );
    }

    protected VideoGameXmlModel prepareExpectedVideoGameXmlResponse(VideoGameDbModel dbModel) {
        VideoGameXmlModel xmlModel = new VideoGameXmlModel();
        xmlModel.setId(dbModel.getId());
        xmlModel.setName(dbModel.getName());
        xmlModel.setReleaseDate(dbModel.getReleaseDateAsString());
        xmlModel.setReviewScore(dbModel.getReviewScore());
        xmlModel.setCategory(dbModel.getCategory());
        xmlModel.setRating(dbModel.getRating());
        return xmlModel;
    }
}

