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

import static com.ai.tester.data.fixtures.VideoGameTestDataFixtures.GRAN_TURISMO_3;
import static com.ai.tester.data.fixtures.VideoGameTestDataFixtures.RESIDENT_EVIL_4;

@Log4j2
public abstract class GetVideoGameByIdBaseTest extends ApiBaseTest {

    protected static final VideoGameTestDataFixtures GAME_1 = RESIDENT_EVIL_4;
    protected static final VideoGameTestDataFixtures GAME_2 = GRAN_TURISMO_3;
    protected static final int NON_EXISTENT_GAME_ID = 99999;
    protected static final String INVALID_GAME_ID = "abc";

    @Autowired
    protected GetVideoGameByIdActions apiActions;

    @SuppressWarnings("all")
    protected String getVideoGamePathPrefix(int gameId) {
        return clientBasePath + Endpoint.VIDEOGAMES.getPath() + "/" + gameId;
    }

    protected VideoGameApiModel prepareExpectedVideoGameResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toApiModel(dbModel);
    }

    protected VideoGameXmlModel prepareExpectedVideoGameXmlResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toXmlModel(dbModel);
    }

}
