package com.ai.tester.client.db;

import com.ai.tester.model.db.VideoGameDbModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class H2DbClient implements DbClient {

    private static final String SELECT_ALL = "SELECT * FROM VIDEOGAME";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE ID = ?";
    private static final String INSERT = "INSERT INTO VIDEOGAME VALUES (?, ?, ?, ?, ?, ?)";
    private static final String DELETE_BY_ID = "DELETE FROM VIDEOGAME WHERE ID = ?";
    private static final String DELETE_ALL = "DELETE FROM VIDEOGAME";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<VideoGameDbModel> getAllVideoGames() {
        try {
            log.debug("Fetching all video games from DB");
            return jdbcTemplate.queryForList(SELECT_ALL)
                .stream()
                .map(this::toVideoGame)
                .toList();
        } catch (DataAccessException e) {
            log.error("Database error fetching all games", e);
            throw new RuntimeException("Failed to fetch video games from database", e);
        }
    }

    @Override
    public Optional<VideoGameDbModel> getVideoGameById(int id) {
        try {
            log.debug("Fetching video game by id={} from DB", id);
            return jdbcTemplate.queryForList(SELECT_BY_ID, id)
                .stream()
                .map(this::toVideoGame)
                .findFirst();
        } catch (DataAccessException e) {
            log.error("Database error fetching game id={}", id, e);
            throw new RuntimeException("Failed to fetch video game from database", e);
        }
    }

    @Override
    public void insertVideoGame(VideoGameDbModel videoGame) {
        try {
            log.debug("Inserting video game id={} into DB", videoGame.getId());
            jdbcTemplate.update(INSERT,
                videoGame.getId(),
                videoGame.getName(),
                new Date(videoGame.getReleaseDate()),
                videoGame.getReviewScore(),
                videoGame.getCategory(),
                videoGame.getRating());
        } catch (DataAccessException e) {
            log.error("Database error inserting game id={}", videoGame.getId(), e);
            throw new RuntimeException("Failed to insert video game into database", e);
        }
    }

    @Override
    public void deleteVideoGameById(int id) {
        try {
            log.debug("Deleting video game by id={} from DB", id);
            jdbcTemplate.update(DELETE_BY_ID, id);
        } catch (DataAccessException e) {
            log.error("Database error deleting game id={}", id, e);
            throw new RuntimeException("Failed to delete video game from database", e);
        }
    }

    @Override
    public void deleteAllVideoGames() {
        try {
            log.debug("Deleting all video games from DB");
            jdbcTemplate.update(DELETE_ALL);
        } catch (DataAccessException e) {
            log.error("Database error deleting all games", e);
            throw new RuntimeException("Failed to delete all video games from database", e);
        }
    }

    private VideoGameDbModel toVideoGame(Map<String, Object> row) {
        try {
            return objectMapper.convertValue(row, VideoGameDbModel.class);
        } catch (IllegalArgumentException e) {
            log.error("Failed to convert database row to VideoGameDbModel: {}", row, e);
            throw new RuntimeException("Database mapping failed", e);
        }
    }
}
