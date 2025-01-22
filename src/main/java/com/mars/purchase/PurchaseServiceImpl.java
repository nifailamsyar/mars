package com.mars.purchase;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mars.common.entity.PurchasedScore;
import com.mars.common.entity.Score;
import com.mars.common.entity.User;
import com.mars.common.interfaces.PurchaseService;
import com.mars.score.ScoreRepository;
import com.mars.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PurchaseServiceImpl implements PurchaseService {
    
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    @Autowired
    public PurchaseServiceImpl(
            PurchaseRepository purchaseRepository,
            UserRepository userRepository,
            ScoreRepository scoreRepository) {
        this.purchaseRepository = purchaseRepository;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
    }

    @Override
    @Transactional
    public void purchaseScore(String username, Long scoreId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Score score = scoreRepository.findById(scoreId)
            .orElseThrow(() -> new RuntimeException("Score not found"));

        if (purchaseRepository.existsByUserIdAndScoreId(user.getId(), scoreId)) {
            throw new RuntimeException("Score already purchased");
        }

        PurchasedScore purchasedScore = new PurchasedScore();
        purchasedScore.setUser(user);
        purchasedScore.setScore(score);
        purchasedScore.setPurchaseDate(LocalDateTime.now());

        purchaseRepository.save(purchasedScore);
    }

    @Override
    public List<PurchasedScore> getUserPurchases(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return purchaseRepository.findByUserIdOrderByPurchaseDateDesc(user.getId());
    }

    @Override
    @Transactional
    public void deletePurchase(String username, Long scoreId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        purchaseRepository.deleteByUserIdAndScoreId(user.getId(), scoreId);
    }

    @Override
    public boolean hasPurchased(String username, Long scoreId) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        return purchaseRepository.existsByUserIdAndScoreId(user.getId(), scoreId);
    }
}
