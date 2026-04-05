package com.ai.tester.getVideoGameById;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.get.byId.GetByIdApiActions;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.VideoGameModelMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
public abstract class GetVideoGameByIdBaseTest extends ApiBaseTest {

    protected static final int NON_EXISTING_GAME_ID = 9999;
    protected static final String NON_INTEGER_GAME_ID = "abc";

    @Autowired
    protected GetByIdApiActions byIdApiActions;

    protected VideoGameDbModel getFirstVideoGameFromDatabase() {
        List<VideoGameDbModel> allGames = dbClient.getAllVideoGames();
        if (allGames.isEmpty()) {
            throw new RuntimeException("Database should contain at least one video game");
        }
        return allGames.getFirst();
    }

    protected VideoGameApiModel prepareExpectedVideoGameResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toApiModel(dbModel);
    }

    protected VideoGameXmlModel prepareExpectedVideoGameXmlResponse(VideoGameDbModel dbModel) {
        return VideoGameModelMapper.toXmlModel(dbModel);
    }
}
