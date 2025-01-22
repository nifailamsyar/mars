package com.mars.common.interfaces;

import org.springframework.web.multipart.MultipartFile;

import com.mars.common.entity.Score;

import java.io.IOException;
import java.util.List;

public interface ScoreService {
    String saveTempFile(MultipartFile file) throws IOException;
    Score saveScore(String title, String composer, String genre, String instrumentation,
    String emotion, String tempFilePath) throws IOException;
    List<Score> getAllScores(String sortBy, String sortDirection);
    Score getScoreById(Long id);
    void deleteScore(Long id) throws IOException;
    Score updateScore(Long id, String username, String title, 
                     String composer, String genre, String instrumentation, 
                     String emotion);
}