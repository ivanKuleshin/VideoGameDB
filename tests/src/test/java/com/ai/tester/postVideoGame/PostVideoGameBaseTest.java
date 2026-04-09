package com.ai.tester.postVideoGame;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.post.PostVideoGameActions;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.model.api.json.PostVideoGameRequestModel;
import com.ai.tester.model.api.json.PostVideoGameResponseModel;
import com.ai.tester.model.api.xml.PostVideoGameXmlRequestModel;
import com.ai.tester.model.api.xml.PostVideoGameXmlResponseModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.DateUtil;
import com.ai.tester.util.XmlUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;


@Log4j2
public abstract class PostVideoGameBaseTest extends ApiBaseTest {

    protected static final String EXPECTED_POST_STATUS = "Record Added Successfully";

    protected static final VideoGameTestDataFixtures JSON_DB_FIXTURE = VideoGameTestDataFixtures.PORTAL_2;
    protected static final VideoGameTestDataFixtures XML_FIXTURE = VideoGameTestDataFixtures.HALO_3;
    protected static final VideoGameTestDataFixtures ID_ONLY_FIXTURE = VideoGameTestDataFixtures.POST_ID_ONLY_GAME;
    protected static final VideoGameTestDataFixtures DUPLICATE_GAME_FIXTURE = VideoGameTestDataFixtures.DUPLICATE_GAME;

    @Autowired
    protected PostVideoGameActions apiActions;

    protected PostVideoGameResponseModel prepareExpectedPostJsonResponse() {
        PostVideoGameResponseModel expectedResponse = new PostVideoGameResponseModel();
        expectedResponse.setStatus(EXPECTED_POST_STATUS);
        return expectedResponse;
    }

    protected PostVideoGameXmlResponseModel prepareExpectedPostXmlResponse() {
        PostVideoGameXmlResponseModel expectedResponse = new PostVideoGameXmlResponseModel();
        expectedResponse.setStatus(EXPECTED_POST_STATUS);
        return expectedResponse;
    }

    protected VideoGameDbModel prepareExpectedGameDbModel(PostVideoGameRequestModel request) {
        VideoGameDbModel expectedGame = new VideoGameDbModel();
        expectedGame.setId(request.getId());
        expectedGame.setName(request.getName());
        if (request.getReleaseDate() != null) {
            expectedGame.setReleaseDate(DateUtil.dateStringToEpochMillis(request.getReleaseDate()));
        }
        expectedGame.setReviewScore(request.getReviewScore());
        expectedGame.setCategory(request.getCategory());
        expectedGame.setRating(request.getRating());
        return expectedGame;
    }

    protected VideoGameDbModel prepareExpectedGameDbModel(PostVideoGameXmlRequestModel request) {
        VideoGameDbModel expectedGame = new VideoGameDbModel();
        expectedGame.setId(request.getId());
        expectedGame.setName(request.getName());
        if (request.getReleaseDate() != null) {
            expectedGame.setReleaseDate(DateUtil.dateStringToEpochMillis(request.getReleaseDate()));
        }
        expectedGame.setReviewScore(request.getReviewScore());
        expectedGame.setCategory(request.getCategory());
        expectedGame.setRating(request.getRating());
        return expectedGame;
    }

    protected VideoGameDbModel prepareExpectedIdOnlyDbModel(PostVideoGameRequestModel request) {
        VideoGameDbModel expectedGame = new VideoGameDbModel();
        expectedGame.setId(request.getId());
        expectedGame.setReviewScore(0);
        return expectedGame;
    }

    protected PostVideoGameRequestModel prepareVideoGameRequest(VideoGameTestDataFixtures fixture) {
        return PostVideoGameRequestModel.builder()
            .id(fixture.getId())
            .name(fixture.getName())
            .releaseDate(fixture.getReleaseDateString())
            .reviewScore(fixture.getReviewScore())
            .category(fixture.getCategory())
            .rating(fixture.getRating())
            .build();
    }

    protected PostVideoGameRequestModel prepareIdOnlyVideoGameRequest(VideoGameTestDataFixtures fixture) {
        PostVideoGameRequestModel request = new PostVideoGameRequestModel();
        request.setId(fixture.getId());
        return request;
    }

    protected PostVideoGameXmlRequestModel prepareXmlVideoGameRequest(VideoGameTestDataFixtures fixture) {
        return new PostVideoGameXmlRequestModel(
            fixture.getId(),
            fixture.getName(),
            fixture.getReleaseDateString(),
            fixture.getReviewScore(),
            fixture.getCategory(),
            fixture.getRating()
        );
    }

    protected String serializeXmlRequest(PostVideoGameXmlRequestModel request) {
        return XmlUtil.serialize(request);
    }
}
