package com.mars.score;

import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.mars.common.entity.Score;
import com.mars.common.interfaces.ScoreService;
import com.mars.util.JwtUtil;

import java.util.Map;
import java.util.stream.Collectors;       

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/scores")
public class ScoreController {
    private static final Logger logger = LoggerFactory.getLogger(ScoreController.class);

    private final JwtUtil jwtUtil;
    private final ScoreService scoreService;

    @Autowired
    public ScoreController(JwtUtil jwtUtil,ScoreService scoreService) {
        this.jwtUtil = jwtUtil;
        this.scoreService = scoreService;
    }

   
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Test endpoint called");
        return ResponseEntity.ok("Score controller is working");
    }

    @GetMapping("/upload-score")
    public String showUploadPage() {
        return "upload-score";
    }

    @GetMapping("/catalog-score")
    public String showCatalogPage(
        @RequestParam(value = "tempFilePath", required = false) String tempFilePath,
        @RequestParam(value = "id", required = false) Long id,
        Model model) {
    if (tempFilePath != null) {
        model.addAttribute("tempFilePath", tempFilePath);
    }
    if (id != null) {
        model.addAttribute("scoreId", id);
    }
    return "catalog-score";
}

    @PostMapping("/upload-temp")
    public ResponseEntity<?> uploadTempFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("scoreFile") MultipartFile file) {
        
        logger.info("Upload temp file request received");
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.error("Invalid token provided for upload");
                return ResponseEntity.badRequest().body("Invalid token");
            }

            String tempFilePath = scoreService.saveTempFile(file);
            logger.info("File temporarily saved at: {}", tempFilePath);
            return ResponseEntity.ok().body(tempFilePath);
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            return ResponseEntity.badRequest().body("Error uploading file: " + e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveScore(
            @RequestHeader("Authorization") String token,
            @RequestParam("tempFilePath") String tempFilePath,
            @RequestParam("title") String title,
            @RequestParam("composer") String composer,
            @RequestParam("genre") String genre,
            @RequestParam("instrumentation") String instrumentation,
            @RequestParam(value = "emotion", required = false) String emotion) {
        
        logger.info("Save score request received for title: {}", title);
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.error("Invalid token provided for save operation");
                return ResponseEntity.badRequest().body("Invalid token");
            }

            String username = jwtUtil.extractUsername(jwtToken);
            logger.info("Saving score for user: {}", username);
            
            Score score = scoreService.saveScore(title, composer, genre, instrumentation, emotion, tempFilePath);

            
            logger.info("Score saved successfully with ID: {}", score.getId());
            return ResponseEntity.ok().body(score);
        } catch (Exception e) {
            logger.error("Error saving score", e);
            return ResponseEntity.badRequest().body("Error saving score: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllScores(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        
        logger.info("Get all scores request received with sort: {} {}", sortBy, sortDirection);
        try {
            String jwtToken = token.substring(7);
            if (!jwtUtil.validateToken(jwtToken)) {
                logger.error("Invalid token provided for getAllScores");
                return ResponseEntity.badRequest().body("Invalid token");
            }
    
            List<Score> scores = scoreService.getAllScores(sortBy, sortDirection);
            
            List<Map<String, Object>> responseList = scores.stream().map(score -> {
                Map<String, Object> scoreMap = new HashMap<>();
                scoreMap.put("id", score.getId());
                scoreMap.put("title", score.getTitle());
                scoreMap.put("composer", score.getComposer());
                scoreMap.put("genre", score.getGenre());
                scoreMap.put("instrumentation", score.getInstrumentation());
                scoreMap.put("emotion", score.getEmotion());
                scoreMap.put("uploadDate", score.getUploadDate());
                scoreMap.put("filePath", score.getFilePath());
                
                return scoreMap;
            }).collect(Collectors.toList());
    
            logger.info("Retrieved {} scores", scores.size());
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            logger.error("Error fetching scores", e);
            return ResponseEntity.badRequest().body("Error fetching scores: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
public ResponseEntity<?> getScoreById(
        @RequestHeader("Authorization") String token,
        @PathVariable Long id) {
    
    logger.info("Get score request received for ID: {}", id);
    try {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            logger.error("Invalid token provided for getScoreById");
            return ResponseEntity.badRequest().body("Invalid token");
        }

        Score score = scoreService.getScoreById(id);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("score", score);
        response.put("filePath", score.getFilePath());
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        logger.error("Error fetching score", e);
        return ResponseEntity.badRequest().body("Error fetching score: " + e.getMessage());
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<?> deleteScore(
        @RequestHeader("Authorization") String token,
        @PathVariable Long id) {
    
    logger.info("Delete score request received for ID: {}", id);
    try {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            logger.error("Invalid token provided for deleteScore");
            return ResponseEntity.badRequest().body("Invalid token");
        }

        
        scoreService.deleteScore(id);
        return ResponseEntity.ok().build();
    } catch (Exception e) {
        logger.error("Error deleting score", e);
        return ResponseEntity.badRequest().body("Error deleting score: " + e.getMessage());
    }
}


@PutMapping("/{id}")
public ResponseEntity<?> updateScore(
        @RequestHeader("Authorization") String token,
        @PathVariable Long id,
        @RequestParam("title") String title,
        @RequestParam("composer") String composer,
        @RequestParam("genre") String genre,
        @RequestParam("instrumentation") String instrumentation,
        @RequestParam(value = "emotion", required = false) String emotion) {
    
    logger.info("Update score request received for ID: {}", id);
    try {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            logger.error("Invalid token provided for updateScore");
            return ResponseEntity.badRequest().body("Invalid token");
        }

        String username = jwtUtil.extractUsername(jwtToken);
        Score updatedScore = scoreService.updateScore(id, username, title, 
                                                    composer, genre, instrumentation, emotion);
        return ResponseEntity.ok(updatedScore);
    } catch (Exception e) {
        logger.error("Error updating score", e);
        return ResponseEntity.badRequest().body("Error updating score: " + e.getMessage());
    }
}

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        logger.error("File size exceeded maximum limit", exc);
        return ResponseEntity.badRequest().body("File too large! Maximum size is 5MB");
    }
}