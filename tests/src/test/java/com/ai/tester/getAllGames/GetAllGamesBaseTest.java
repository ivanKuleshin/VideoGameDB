package com.ai.tester.getAllGames;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.get.getAll.GetAllGamesActions;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.api.xml.VideoGameXmlModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.VideoGameModelMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
public abstract class GetAllGamesBaseTest extends ApiBaseTest {

    @Autowired
    protected GetAllGamesActions apiActions;

    protected List<VideoGameApiModel> prepareExpectedAllGamesResponseList(List<VideoGameDbModel> allVideoGames) {
        return allVideoGames.stream()
            .map(VideoGameModelMapper::toApiModel)
            .toList();
    }

    protected List<VideoGameXmlModel> prepareExpectedAllGamesXmlResponseList(List<VideoGameDbModel> allVideoGames) {
        return allVideoGames.stream()
            .map(VideoGameModelMapper::toXmlModel)
            .toList();
    }

    protected List<VideoGameDbModel> prepareDatabaseSnapshot() {
        return dbClient.getAllVideoGames();
    }
}
