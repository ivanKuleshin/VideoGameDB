package com.ai.tester.postVideoGame;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.post.PostVideoGameApiActions;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.model.api.json.PostVideoGameRequestModel;
import com.ai.tester.model.api.json.PostVideoGameResponseModel;
import com.ai.tester.model.api.xml.PostVideoGameXmlRequestModel;
import com.ai.tester.model.api.xml.PostVideoGameXmlResponseModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.DateUtil;
import com.ai.tester.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class PostVideoGameBaseTest extends ApiBaseTest {

    protected static final String EXPECTED_POST_STATUS = "Record Added Successfully";

    @Autowired
    protected PostVideoGameApiActions apiActions;

    protected VideoGameTestDataFixtures getJsonDbFixture() {
        return VideoGameTestDataFixtures.PORTAL_2;
    }

    protected VideoGameTestDataFixtures getXmlFixture() {
        return VideoGameTestDataFixtures.HALO_3;
    }

    protected VideoGameTestDataFixtures getIdOnlyFixture() {
        return VideoGameTestDataFixtures.POST_ID_ONLY_GAME;
    }

    protected VideoGameTestDataFixtures getDuplicateGameFixture() {
        return VideoGameTestDataFixtures.DUPLICATE_GAME;
    }

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
        return new PostVideoGameRequestModel(
            fixture.getId(),
            fixture.getName(),
            fixture.getReleaseDateString(),
            fixture.getReviewScore(),
            fixture.getCategory(),
            fixture.getRating()
        );
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
