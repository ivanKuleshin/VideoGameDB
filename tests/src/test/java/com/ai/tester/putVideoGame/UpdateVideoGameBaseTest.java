package com.ai.tester.putVideoGame;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.put.UpdateVideoGameActions;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.model.api.json.UpdateVideoGameRequestModel;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.UpdateVideoGameXmlRequestModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.DateUtil;
import com.ai.tester.util.XmlUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public abstract class UpdateVideoGameBaseTest extends ApiBaseTest {

    protected static final int NON_EXISTING_GAME_ID = 99999;
    protected static final String NON_INTEGER_GAME_ID = "abc";

    protected static final VideoGameTestDataFixtures JSON_INITIAL_FIXTURE = VideoGameTestDataFixtures.PUT_JSON_INITIAL;
    protected static final VideoGameTestDataFixtures JSON_UPDATE_FIXTURE = VideoGameTestDataFixtures.PUT_JSON_UPDATED;
    protected static final VideoGameTestDataFixtures XML_INITIAL_FIXTURE = VideoGameTestDataFixtures.PUT_XML_INITIAL;
    protected static final VideoGameTestDataFixtures XML_UPDATE_FIXTURE = VideoGameTestDataFixtures.PUT_XML_UPDATED;
    protected static final VideoGameTestDataFixtures PATH_PRIMARY_FIXTURE = VideoGameTestDataFixtures.PUT_PATH_PRIMARY;
    protected static final VideoGameTestDataFixtures PATH_SECONDARY_FIXTURE = VideoGameTestDataFixtures.PUT_PATH_SECONDARY;
    protected static final VideoGameTestDataFixtures MISSING_AUTH_FIXTURE = VideoGameTestDataFixtures.PUT_MISSING_AUTH;
    protected static final VideoGameTestDataFixtures WRONG_AUTH_FIXTURE = VideoGameTestDataFixtures.PUT_WRONG_AUTH;

    @Autowired
    protected UpdateVideoGameActions apiActions;

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

    protected VideoGameXmlModel prepareExpectedXmlModel(int gameId, VideoGameTestDataFixtures updateFixture) {
        return new VideoGameXmlModel(
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
