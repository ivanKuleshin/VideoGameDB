package com.ai.tester.deleteVideoGame;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.delete.DeleteByIdActions;
import com.ai.tester.actions.api.get.getAll.GetAllGamesApiActions;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteVideoGameBaseTest extends ApiBaseTest {

    protected static final String EXPECTED_DELETE_STATUS = "Record Deleted Successfully";

    protected static final VideoGameTestDataFixtures PRIMARY_GAME = VideoGameTestDataFixtures.SHOOTER_GAME;
    protected static final VideoGameTestDataFixtures SECONDARY_GAME = VideoGameTestDataFixtures.PUZZLE_GAME;

    @Autowired
    protected DeleteByIdActions apiActions;

    @Autowired
    protected GetAllGamesApiActions getAllGamesApiActions;
}

