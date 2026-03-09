package com.ai.tester.data.fixtures;

import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.DateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VideoGameTestDataFixtures {

    SHOOTER_GAME(101, "Doom Test", "1993-02-18", 81, "Shooter", "Mature"),
    PUZZLE_GAME(102, "Minecraft Test", "2011-12-05", 77, "Puzzle", "Universal"),
    ACTION_RPG(103, "Dark Souls Test", "2011-09-22", 89, "Action RPG", "Mature"),
    INDIE_GAME(104, "Stardew Valley Test", "2016-02-26", 85, "Simulation", "Universal"),
    STRATEGY_GAME(105, "SimCity Test", "1994-09-11", 88, "Strategy", "Universal");

    private final int id;
    private final String name;
    private final String releaseDateString;
    private final int reviewScore;
    private final String category;
    private final String rating;

    public VideoGameDbModel getGameData() {
        VideoGameDbModel game = new VideoGameDbModel();
        game.setId(this.id);
        game.setName(this.name);
        game.setReleaseDate(DateUtil.dateStringToEpochMillis(this.releaseDateString));
        game.setReviewScore(this.reviewScore);
        game.setCategory(this.category);
        game.setRating(this.rating);
        return game;
    }
}
