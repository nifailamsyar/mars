
package com.mars.score;

import com.mars.common.entity.Score;
import com.mars.common.interfaces.ScoreService;
import com.mars.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository scoreRepository;
    private final String uploadDir;
    private final String tempDir;

    @Autowired
    public ScoreServiceImpl(ScoreRepository scoreRepository,
                          UserRepository userRepository,
                          @Value("${file.upload-dir}") String uploadDir,
                          @Value("${file.temp-dir}") String tempDir) {
        this.scoreRepository = scoreRepository;
        this.uploadDir = uploadDir;
        this.tempDir = tempDir;
    }

    @Override
    public String saveTempFile(MultipartFile file) throws IOException {
       
       Path tempPath = Paths.get(tempDir);
       if (!Files.exists(tempPath)) {
           Files.createDirectories(tempPath);
       }
   
       
       String originalFileName = file.getOriginalFilename();
       if (originalFileName == null) {
           originalFileName = "unknown_file";
       }
       String fileExtension = originalFileName.contains(".") ? 
           originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
       String tempFileName = "temp_" + UUID.randomUUID().toString() + fileExtension;
       Path filePath = tempPath.resolve(tempFileName);
   
       
       Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
   
       return tempFileName;
   
    }

    @Override
public Score saveScore(String title, String composer, String genre, String instrumentation,
                       String emotion, String tempFilePath) throws IOException {


Path uploadPath = Paths.get(uploadDir);  
if (!Files.exists(uploadPath)) {
 Files.createDirectories(uploadPath);
}


Path tempFile = Paths.get(tempDir, tempFilePath);
String newFileName = UUID.randomUUID().toString() + 
                 tempFilePath.substring(tempFilePath.lastIndexOf("."));
Path permanentFile = uploadPath.resolve(newFileName);

Files.move(tempFile, permanentFile, StandardCopyOption.REPLACE_EXISTING);


Score score = new Score();
score.setTitle(title);
score.setComposer(composer);
score.setGenre(genre);
score.setInstrumentation(instrumentation);
score.setEmotion(emotion);
score.setUploadDate(LocalDateTime.now());
score.setFilePath(newFileName);

Score savedScore = scoreRepository.save(score);

return savedScore;    }

    @Override
    public List<Score> getAllScores(String sortBy, String sortDirection) {
        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            
            
            String sortField = sortBy.equals("uploadDate") ? "uploadDate" : sortBy;
            return scoreRepository.findAll(Sort.by(direction, sortField));
        }
        
        
        return scoreRepository.findAllByOrderByUploadDateDesc();
    }

    @Override
    public Score getScoreById(Long id) {
        return scoreRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Score not found"));    }

    @Override
    public void deleteScore(Long id) throws IOException {
 Score score = scoreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Score not found"));

    
    scoreRepository.delete(score);    }

    @Override
    public Score updateScore(Long id, String username, String title, 
                           String composer, String genre, String instrumentation, 
                           String emotion) {
        Score score = scoreRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Score not found"));
    
    
    score.setTitle(title);
    score.setComposer(composer);
    score.setGenre(genre);
    score.setInstrumentation(instrumentation);
    score.setEmotion(emotion);

    return scoreRepository.save(score);
}
}