package com.ai.tester.repository;

import com.ai.tester.model.VideoGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface VideoGameRepository extends JpaRepository<VideoGame, Integer> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(
        value = "DELETE FROM VIDEOGAME WHERE id IN "
            + "(SELECT id FROM VIDEOGAME WHERE MOD(id, 2) = 0 LIMIT 5)",
        nativeQuery = true
    )
    int deleteEvenGamesLimited();
}

