package com.ai.tester.data.fixtures;

import com.ai.tester.model.db.VideoGameDbModel;
import com.ai.tester.util.DateUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum VideoGameTestDataFixtures {

    RESIDENT_EVIL_4(1, "Resident Evil 4", "2005-10-01", 85, "Shooter", "Universal"),
    GRAN_TURISMO_3(2, "Gran Turismo 3", "2001-03-10", 91, "Driving", "Universal"),
    SHOOTER_GAME(101, "Doom Test", "1993-02-18", 81, "Shooter", "Mature"),
    PUZZLE_GAME(102, "Minecraft Test", "2011-12-05", 77, "Puzzle", "Universal"),
    ACTION_RPG(103, "Dark Souls Test", "2011-09-22", 89, "Action RPG", "Mature"),
    INDIE_GAME(104, "Stardew Valley Test", "2016-02-26", 85, "Simulation", "Universal"),
    STRATEGY_GAME(105, "SimCity Test", "1994-09-11", 88, "Strategy", "Universal"),
    HALF_LIFE_2(106, "Half-Life 2", "2004-11-16", 96, "Shooter", "Mature"),
    PORTAL_2(107, "Portal 2", "2011-04-19", 95, "Puzzle", "Universal"),
    HALO_3(108, "Halo 3", "2007-09-25", 94, "Shooter", "Mature"),
    DOOM_ETERNAL(109, "Doom Eternal", "2020-03-20", 88, "Shooter", "Mature"),
    SEKIRO(110, "Sekiro", "2019-03-22", 90, "Action-Adventure", "Mature"),
    POST_ID_ONLY_GAME(111, null, null, null, null, null),
    DUPLICATE_GAME(1, "Duplicate Test Game", "2024-01-01", 50, "Puzzle", "Universal"),
    PUT_JSON_INITIAL(112, "Test Game AC1", "2000-01-01", 70, "Action", "Universal"),
    PUT_JSON_UPDATED(112, "Test Game AC1 Updated", "2023-03-24", 93, "Shooter", "Mature"),
    PUT_XML_INITIAL(113, "Test Game AC3", "2000-01-01", 70, "Puzzle", "Universal"),
    PUT_XML_UPDATED(113, "Tetris Effect", "2018-11-09", 91, "Puzzle", "Universal"),
    PUT_PATH_PRIMARY(114, "Test Game AC4 Primary", "2000-01-01", 70, "Action", "Universal"),
    PUT_PATH_SECONDARY(115, "Test Game AC4 Secondary", "2000-01-01", 70, "Action", "Universal"),
    PUT_MISSING_AUTH(116, "Test Game AC5", "2000-01-01", 70, "Action", "Universal"),
    PUT_WRONG_AUTH(117, "Test Game AC6", "2000-01-01", 70, "Action", "Universal");

    private final int id;
    private final String name;
    private final String releaseDateString;
    private final Integer reviewScore;
    private final String category;
    private final String rating;

    public VideoGameDbModel getGameData() {
        VideoGameDbModel game = new VideoGameDbModel();
        game.setId(this.id);
        game.setName(this.name);
        if (this.releaseDateString != null) {
            game.setReleaseDate(DateUtil.dateStringToEpochMillis(this.releaseDateString));
        }
        game.setReviewScore(this.reviewScore);
        game.setCategory(this.category);
        game.setRating(this.rating);
        return game;
    }
}
