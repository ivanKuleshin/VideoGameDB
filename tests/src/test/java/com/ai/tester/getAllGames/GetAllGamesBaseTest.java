package com.ai.tester.getAllGames;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.get.getAll.GetAllGamesApiActions;
import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.VideoGameModelMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Log4j2
public abstract class GetAllGamesBaseTest extends ApiBaseTest {

    @Autowired
    protected GetAllGamesApiActions apiActions;

    protected List<VideoGameApiModel> prepareExpectedAllGamesResponseList(List<VideoGameDbModel> allVideoGames) {
        return allVideoGames.stream()
            .map(VideoGameModelMapper::toApiModel)
            .toList();
    }

    protected List<VideoGameDbModel> prepareDatabaseSnapshot() {
        return dbClient.getAllVideoGames();
    }
}
