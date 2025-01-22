package com.mars.purchase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mars.common.entity.PurchasedScore;
import com.mars.common.entity.Score;
import com.mars.common.interfaces.PurchaseService;
import com.mars.purchase.dto.PurchaseDTO;
import com.mars.util.JwtUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/purchases")
public class PurchaseController {
    
    private final PurchaseService purchaseService;
    private final JwtUtil jwtUtil;

    @Autowired
    public PurchaseController(PurchaseService purchaseService, JwtUtil jwtUtil) {
        this.purchaseService = purchaseService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/{scoreId}")
    public ResponseEntity<?> purchaseScore(
            @RequestHeader("Authorization") String token,
            @PathVariable Long scoreId) {
        try {
            String username = extractUsername(token);
            purchaseService.purchaseScore(username, scoreId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

 @GetMapping
public ResponseEntity<?> getPurchasedScores(@RequestHeader("Authorization") String token) {
    try {
        String username = extractUsername(token);
        List<PurchasedScore> purchases = purchaseService.getUserPurchases(username);
        
        List<PurchaseDTO> response = purchases.stream().map(ps -> {
            PurchaseDTO dto = new PurchaseDTO();
            Score score = ps.getScore();
            
            dto.setId(ps.getId());
            dto.setScoreId(score.getId());
            dto.setTitle(score.getTitle());
            dto.setComposer(score.getComposer());
            dto.setGenre(score.getGenre());
            dto.setInstrumentation(score.getInstrumentation());
            dto.setEmotion(score.getEmotion());
            dto.setPurchaseDate(ps.getPurchaseDate());
            
            List<Map<String, Object>> filesInfo = new ArrayList<>();
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("filePath", score.getFilePath());
            filesInfo.add(fileMap);
            
            dto.setFiles(filesInfo);
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    @DeleteMapping("/{scoreId}")
    public ResponseEntity<?> deletePurchasedScore(
            @RequestHeader("Authorization") String token,
            @PathVariable Long scoreId) {
        try {
            String username = extractUsername(token);
            purchaseService.deletePurchase(username, scoreId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{scoreId}/check")
    public ResponseEntity<?> checkPurchaseStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long scoreId) {
        try {
            String username = extractUsername(token);
            boolean hasPurchased = purchaseService.hasPurchased(username, scoreId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("purchased", hasPurchased);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        if (!jwtUtil.validateToken(jwtToken)) {
            throw new RuntimeException("Invalid token");
        }
        return jwtUtil.extractUsername(jwtToken);
    }
}