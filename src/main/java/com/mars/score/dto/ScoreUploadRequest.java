package com.mars.score.dto;

import org.springframework.web.multipart.MultipartFile;

public class ScoreUploadRequest {
    private String title;
    private String composer;
    private String genre;
    private String instrumentation;
    private String emotion;
    private MultipartFile scoreFile;

    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getInstrumentation() {
        return instrumentation;
    }

    public void setInstrumentation(String instrumentation) {
        this.instrumentation = instrumentation;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public MultipartFile getScoreFile() {
        return scoreFile;
    }

    public void setScoreFile(MultipartFile scoreFile) {
        this.scoreFile = scoreFile;
    }
}