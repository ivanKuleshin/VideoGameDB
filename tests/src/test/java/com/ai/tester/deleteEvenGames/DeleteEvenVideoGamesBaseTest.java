package com.ai.tester.deleteEvenGames;

import com.ai.tester.ApiBaseTest;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class DeleteEvenVideoGamesBaseTest extends ApiBaseTest {

    protected static final int DELETE_LIMIT = 5;
    protected static final String EXPECTED_STATUS_TEMPLATE = "Deleted %d records with even IDs";
}

