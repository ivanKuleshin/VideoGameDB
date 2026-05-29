package com.ai.tester.putVideoGame;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.put.UpdateVideoGameApiActions;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.model.api.json.UpdateVideoGameRequestModel;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.UpdateVideoGameXmlRequestModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.DateUtil;
import com.ai.tester.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class UpdateVideoGameBaseTest extends ApiBaseTest {

    protected static final int NON_EXISTING_GAME_ID = 99999;
    protected static final String NON_INTEGER_GAME_ID = "abc";

    @Autowired
    protected UpdateVideoGameApiActions apiActions;

    protected VideoGameTestDataFixtures getJsonInitialFixture() {
        return VideoGameTestDataFixtures.PUT_JSON_INITIAL;
    }

    protected VideoGameTestDataFixtures getJsonUpdateFixture() {
        return VideoGameTestDataFixtures.PUT_JSON_UPDATED;
    }

    protected VideoGameTestDataFixtures getXmlInitialFixture() {
        return VideoGameTestDataFixtures.PUT_XML_INITIAL;
    }

    protected VideoGameTestDataFixtures getXmlUpdateFixture() {
        return VideoGameTestDataFixtures.PUT_XML_UPDATED;
    }

    protected VideoGameTestDataFixtures getPathPrimaryFixture() {
        return VideoGameTestDataFixtures.PUT_PATH_PRIMARY;
    }

    protected VideoGameTestDataFixtures getPathSecondaryFixture() {
        return VideoGameTestDataFixtures.PUT_PATH_SECONDARY;
    }

    protected VideoGameTestDataFixtures getMissingAuthFixture() {
        return VideoGameTestDataFixtures.PUT_MISSING_AUTH;
    }

    protected VideoGameTestDataFixtures getWrongAuthFixture() {
        return VideoGameTestDataFixtures.PUT_WRONG_AUTH;
    }

    protected UpdateVideoGameRequestModel prepareUpdateRequestBody(VideoGameTestDataFixtures fixture) {
        return UpdateVideoGameRequestModel.builder()
            .id(fixture.getId())
            .name(fixture.getName())
            .releaseDate(fixture.getReleaseDateString())
            .reviewScore(fixture.getReviewScore())
            .category(fixture.getCategory())
            .rating(fixture.getRating())
            .build();
    }

    protected String prepareSerializedXmlBody(VideoGameTestDataFixtures fixture) {
        UpdateVideoGameXmlRequestModel xmlRequest = UpdateVideoGameXmlRequestModel.builder()
            .id(fixture.getId())
            .name(fixture.getName())
            .releaseDate(fixture.getReleaseDateString())
            .reviewScore(fixture.getReviewScore())
            .category(fixture.getCategory())
            .rating(fixture.getRating())
            .build();
        return XmlUtil.serialize(xmlRequest);
    }

    protected VideoGameApiModel prepareExpectedApiModel(int gameId, VideoGameTestDataFixtures updateFixture) {
        return new VideoGameApiModel(
            gameId,
            updateFixture.getName(),
            updateFixture.getReleaseDateString(),
            updateFixture.getReviewScore(),
            updateFixture.getCategory(),
            updateFixture.getRating()
        );
    }

    protected VideoGameDbModel prepareExpectedUpdatedDbModel(int gameId, VideoGameTestDataFixtures updateFixture) {
        VideoGameDbModel model = new VideoGameDbModel();
        model.setId(gameId);
        model.setName(updateFixture.getName());
        if (updateFixture.getReleaseDateString() != null) {
            model.setReleaseDate(DateUtil.dateStringToEpochMillis(updateFixture.getReleaseDateString()));
        }
        model.setReviewScore(updateFixture.getReviewScore());
        model.setCategory(updateFixture.getCategory());
        model.setRating(updateFixture.getRating());
        return model;
    }
}
