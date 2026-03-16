package com.ai.tester.service;

import com.ai.tester.model.VideoGame;
import com.ai.tester.model.VideoGameList;
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
    public void createVideoGame(VideoGame videoGame) {
        videoGameRepository.save(videoGame);
    }

    @Transactional
    public VideoGame updateVideoGame(VideoGame videoGame, int videoGameId) {
        videoGameRepository.save(videoGame);
        return videoGameRepository.findById(videoGameId).orElseThrow();
    }

    @Transactional
    public void deleteVideoGame(int id) {
        videoGameRepository.deleteById(id);
    }

    @Transactional
    public int deleteEvenGames() {
        return videoGameRepository.deleteEvenGamesLimited();
    }
}

