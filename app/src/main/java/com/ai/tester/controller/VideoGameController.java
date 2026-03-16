package com.ai.tester.controller;

import com.ai.tester.model.VideoGame;
import com.ai.tester.model.VideoGameList;
import com.ai.tester.service.VideoGameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(
    path = "/app/videogames",
    produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
)
@Tag(name = "Video Games")
public class VideoGameController {

    private final VideoGameService videoGameService;

    public VideoGameController(VideoGameService videoGameService) {
        this.videoGameService = videoGameService;
    }

    @GetMapping
    @Operation(summary = "Get all video games", description = "Returns all video games in the database")
    public VideoGameList listVideoGames() {
        return videoGameService.getAllVideoGames();
    }

    @GetMapping("/{videoGameId}")
    @Operation(summary = "Get a video game by ID", description = "Returns a single video game by its ID")
    public VideoGame getVideoGame(
        @Parameter(description = "The video game ID", required = true)
        @PathVariable int videoGameId) {
        return videoGameService.getVideoGameById(videoGameId);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @Operation(summary = "Add a new video game", description = "Adds a new video game to the database")
    public Map<String, String> createVideoGame(@RequestBody VideoGame videoGame) {
        videoGameService.createVideoGame(videoGame);
        return Map.of("status", "Record Added Successfully");
    }

    @PutMapping(
        path = "/{videoGameId}",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    @Operation(summary = "Update a video game", description = "Updates an existing video game by ID")
    public VideoGame editVideoGame(@RequestBody VideoGame videoGame, @PathVariable int videoGameId) {
        return videoGameService.updateVideoGame(videoGame, videoGameId);
    }

    @DeleteMapping("/{videoGameId}")
    @Operation(summary = "Delete a video game", description = "Deletes a video game from the database by ID")
    public Map<String, String> deleteVideoGame(
        @Parameter(description = "The video game ID", required = true)
        @PathVariable int videoGameId) {
        videoGameService.deleteVideoGame(videoGameId);
        return Map.of("status", "Record Deleted Successfully");
    }

    @DeleteMapping("/delete-even-games")
    @Operation(
        summary = "Delete even video game IDs",
        description = "Deletes up to 5 video games with even IDs per request"
    )
    public Map<String, String> deleteEvenVideoGames() {
        int deletedCount = videoGameService.deleteEvenGames();
        return Map.of("status", "Deleted " + deletedCount + " records with even IDs");
    }
}

