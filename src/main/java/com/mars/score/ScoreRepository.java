package com.mars.score;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mars.common.entity.Score;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    List<Score> findAllByOrderByUploadDateDesc();
    
    List<Score> findByTitleContainingIgnoreCaseOrComposerContainingIgnoreCaseOrGenreContainingIgnoreCase(
        String title, String composer, String genre);
    

}