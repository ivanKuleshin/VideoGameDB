package com.ai.tester.deleteEvenGames;

import com.ai.tester.ApiBaseTest;
import com.ai.tester.actions.api.delete.DeleteEvenGamesActions;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class DeleteEvenVideoGamesBaseTest extends ApiBaseTest {

    protected static final int DELETE_LIMIT = 5;
    protected static final String EXPECTED_STATUS_TEMPLATE = "Deleted %d records with even IDs";

    @Autowired
    protected DeleteEvenGamesActions apiActions;
}
