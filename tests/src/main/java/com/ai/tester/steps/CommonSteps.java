package com.ai.tester.steps;

import com.ai.tester.allure.AllureSteps;
import com.ai.tester.client.db.DbClient;
import com.ai.tester.model.db.VideoGameDbModel;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Component
public class CommonSteps {

    @Autowired
    private DbClient dbClient;

    public VideoGameDbModel verifyGameExistsInDatabase(Logger log, int gameId) {
        return AllureSteps.logStepAndReturn(log,
            "Verify game with ID " + gameId + " exists in database",
            () -> {
                var found = dbClient.getVideoGameById(gameId);
                assertThat(found)
                    .as("Game with ID %d should exist in the database", gameId)
                    .isPresent();
                assertThat(found.get().getId())
                    .as("Game ID should match")
                    .isEqualTo(gameId);
                return found.get();
            });
    }

    public VideoGameDbModel verifyGameExistsInDatabase(Logger log, int gameId, String expectedName) {
        VideoGameDbModel game = verifyGameExistsInDatabase(log, gameId);
        verifyGameNameMatches(log, game, expectedName);
        return game;
    }

    public void verifyGameNameMatches(Logger log, VideoGameDbModel game, String expectedName) {
        AllureSteps.logStep(log,
            "Verify game with ID " + game.getId() + " has name '" + expectedName + "'",
            () -> assertThat(game.getName())
                .as("Game name should match")
                .isEqualTo(expectedName));
    }

    public Optional<VideoGameDbModel> verifyGameNotExistsInDatabase(Logger log, int gameId) {
        return AllureSteps.logStepAndReturn(log,
            "Verify game with ID " + gameId + " does NOT exist in database",
            () -> {
                var deletedGame = dbClient.getVideoGameById(gameId);
                assertThat(deletedGame)
                    .as("Game with ID %d should not exist in the database", gameId)
                    .isEmpty();
                return deletedGame;
            });
    }

}
