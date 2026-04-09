package com.ai.tester.service;

import com.ai.tester.model.VideoGame;
import com.ai.tester.model.VideoGameList;
import com.ai.tester.model.VideoGameRequest;
import com.ai.tester.repository.VideoGameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoGameService {

    private final VideoGameRepository videoGameRepository;

    public VideoGameService(VideoGameRepository videoGameRepository) {
        this.videoGameRepository = videoGameRepository;
    }

    public VideoGameList getAllVideoGames() {
        return new VideoGameList(videoGameRepository.findAll());
    }

    public VideoGame getVideoGameById(int id) {
        return videoGameRepository.findById(id).orElseThrow();
    }

    @Transactional
    public void createVideoGame(VideoGameRequest request) {
        videoGameRepository.save(toEntity(request));
    }

    @Transactional
    public VideoGame updateVideoGame(VideoGameRequest request, int videoGameId) {
        videoGameRepository.findById(videoGameId).orElseThrow();
        VideoGame entity = toEntity(request);
        entity.setId(videoGameId);
        return videoGameRepository.save(entity);
    }

    @Transactional
    public void deleteVideoGame(int id) {
        videoGameRepository.deleteById(id);
    }

    @Transactional
    public int deleteEvenGames() {
        return videoGameRepository.deleteEvenGamesLimited();
    }

    private VideoGame toEntity(VideoGameRequest request) {
        VideoGame entity = new VideoGame();
        entity.setId(request.getId());
        entity.setName(request.getName());
        entity.setReleaseDate(request.getReleaseDate());
        entity.setReviewScore(request.getReviewScore());
        entity.setCategory(request.getCategory());
        entity.setRating(request.getRating());
        return entity;
    }
}
