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

    protected List<VideoGameApiModel> prepareExpectedAllGamesXmlResponseList(List<VideoGameXmlModel> xmlVideoGames) {
        return xmlVideoGames.stream()
            .map(xmlModel -> new VideoGameApiModel(
                xmlModel.getId(),
                xmlModel.getName(),
                xmlModel.getReleaseDate(),
                xmlModel.getReviewScore(),
                xmlModel.getCategory(),
                xmlModel.getRating()
            ))
            .toList();
    }
}
