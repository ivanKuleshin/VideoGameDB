package com.ai.tester.client.db;

import com.ai.tester.model.db.VideoGameDbModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class H2DbClient implements DbClient {

    private static final String SELECT_ALL = "SELECT * FROM VIDEOGAME";
    private static final String SELECT_BY_ID = SELECT_ALL + " WHERE ID = ?";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<VideoGameDbModel> getAllVideoGames() {
        log.debug("Fetching all video games from DB");
        return jdbcTemplate.queryForList(SELECT_ALL)
            .stream()
            .map(this::toVideoGame)
            .toList();
    }

    @Override
    public Optional<VideoGameDbModel> getVideoGameById(int id) {
        log.debug("Fetching video game by id={} from DB", id);
        return jdbcTemplate.queryForList(SELECT_BY_ID, id)
            .stream()
            .map(this::toVideoGame)
            .findFirst();
    }

    private VideoGameDbModel toVideoGame(Map<String, Object> row) {
        return objectMapper.convertValue(row, VideoGameDbModel.class);
    }
}
