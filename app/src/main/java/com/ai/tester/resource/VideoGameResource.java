package com.ai.tester.resource;

import com.ai.tester.model.VideoGame;
import com.ai.tester.model.VideoGameList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

@Path("/videogames")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Tag(name = "Video Games")
public class VideoGameResource {

    private static final String SQL_SELECT_ALL = "SELECT * FROM VIDEOGAME";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM VIDEOGAME WHERE id = :videoGameId";
    private static final String SQL_INSERT =
        "INSERT INTO VIDEOGAME VALUES (:id, :name, :releaseDate, :reviewScore, :category, :rating)";
    private static final String SQL_UPDATE =
        "UPDATE VIDEOGAME SET name = :name, released_on = :releaseDate, review_score = :reviewScore, category = :category, " +
            "rating = :rating WHERE id = :id";
    private static final String SQL_DELETE = "DELETE FROM VIDEOGAME WHERE id = :videoGameId";

    private static final VideoGameMapper MAPPER = new VideoGameMapper();

    private final NamedParameterJdbcTemplate jdbc;

    @Autowired
    public VideoGameResource(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GET
    @Operation(summary = "Get all video games", description = "Returns all video games in the database")
    public VideoGameList listVideoGames() {
        return new VideoGameList(jdbc.query(SQL_SELECT_ALL, MAPPER));
    }

    @GET
    @Path("/{videoGameId}")
    @Operation(summary = "Get a video game by ID", description = "Returns a single video game by its ID")
    public VideoGame getVideoGame(
        @Parameter(description = "The video game ID", required = true)
        @PathParam("videoGameId") int videoGameId) {
        SqlParameterSource params = new MapSqlParameterSource("videoGameId", videoGameId);
        return jdbc.query(SQL_SELECT_BY_ID, params, MAPPER).getFirst();
    }

    @POST
    @Operation(summary = "Add a new video game", description = "Adds a new video game to the database")
    public String createVideoGame(VideoGame videoGame) {
        jdbc.update(SQL_INSERT, new BeanPropertySqlParameterSource(videoGame));
        return "{\"status\": \"Record Added Successfully\"}";
    }

    @PUT
    @Path("/{videoGameId}")
    @Operation(summary = "Update a video game", description = "Updates an existing video game by ID")
    public VideoGame editVideoGame(VideoGame videoGame, @PathParam("videoGameId") int videoGameId) {
        jdbc.update(SQL_UPDATE, new BeanPropertySqlParameterSource(videoGame));
        SqlParameterSource params = new MapSqlParameterSource("videoGameId", videoGameId);
        return jdbc.query(SQL_SELECT_BY_ID, params, MAPPER).getFirst();
    }

    @DELETE
    @Path("/{videoGameId}")
    @Operation(summary = "Delete a video game", description = "Deletes a video game from the database by ID")
    public String deleteVideoGame(
        @Parameter(description = "The video game ID", required = true)
        @PathParam("videoGameId") int videoGameId) {
        SqlParameterSource params = new MapSqlParameterSource("videoGameId", videoGameId);
        jdbc.update(SQL_DELETE, params);
        return "{\"status\": \"Record Deleted Successfully\"}";
    }

    private static final class VideoGameMapper implements RowMapper<VideoGame> {
        @Override
        public VideoGame mapRow(ResultSet rs, int rowNum) throws SQLException {
            VideoGame videoGame = new VideoGame();
            videoGame.setId(rs.getInt("id"));
            videoGame.setName(rs.getString("name"));
            videoGame.setReleaseDate(rs.getObject("released_on", LocalDate.class));
            videoGame.setReviewScore(rs.getInt("review_score"));
            videoGame.setCategory(rs.getString("category"));
            videoGame.setRating(rs.getString("rating"));
            return videoGame;
        }
    }

    @DELETE
    @Path("/delete-even-games")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete even video game IDs", description = "Deletes up to 5 video games with even IDs per request")
    public Response deleteEvenVideoGames() {
        String sql = "DELETE FROM VIDEOGAME WHERE id IN (SELECT id FROM VIDEOGAME WHERE MOD(id, 2) = 0 LIMIT 5)";
        int deletedCount = jdbc.getJdbcTemplate().update(sql);
        return Response.ok(Map.of("status", "Deleted " + deletedCount + " records with even IDs")).build();
    }
}
