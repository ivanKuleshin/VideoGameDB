package com.ai.tester.deleteVideoGame;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.data.fixtures.VideoGameTestDataFixtures;

public class DeleteVideoGameBaseTest extends ApiBaseTest {

    protected static final String EXPECTED_DELETE_STATUS = "Record Deleted Successfully";

    protected static final VideoGameTestDataFixtures PRIMARY_GAME = VideoGameTestDataFixtures.SHOOTER_GAME;
    protected static final VideoGameTestDataFixtures SECONDARY_GAME = VideoGameTestDataFixtures.PUZZLE_GAME;
    protected static final VideoGameTestDataFixtures GAME_FOR_XML_DELETE = VideoGameTestDataFixtures.STRATEGY_GAME;
}
