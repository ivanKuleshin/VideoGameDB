package com.ai.tester.getVideoGameById;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.get.byId.GetVideoGameByIdActions;
import com.ai.tester.data.Endpoint;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.VideoGameModelMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Log4j2
public abstract class GetVideoGameByIdBaseTest extends ApiBaseTest {

    protected static final VideoGameTestDataFixtures GAME_ID_FOR_STATUS_AND_BODY_TESTS =
        VideoGameTestDataFixtures.RESIDENT_EVIL_4;
    protected static final VideoGameTestDataFixtures GAME_ID_FOR_CONTENT_TYPE_TESTS =
        VideoGameTestDataFixtures.GRAN_TURISMO_3;
    protected static final int NON_EXISTENT_GAME_ID = 99999;
    protected static final String INVALID_GAME_ID = "abc";

    @Value("${http.client.base-path}")
    private String clientBasePath;

    @Autowired
    protected GetVideoGameByIdActions apiActions;

    protected String getVideoGamePathPrefix() {
        return clientBasePath + Endpoint.VIDEOGAMES.getPath() + "/";
    }

    protected VideoGameApiModel prepareExpectedVideoGameResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toApiModel(dbModel);
    }

    protected VideoGameXmlModel prepareExpectedVideoGameXmlResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toXmlModel(dbModel);
    }

}
