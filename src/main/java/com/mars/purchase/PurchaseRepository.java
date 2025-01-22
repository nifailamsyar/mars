package com.mars.purchase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mars.common.entity.PurchasedScore;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchasedScore, Long> {
    List<PurchasedScore> findByUserIdOrderByPurchaseDateDesc(Long userId);
    Optional<PurchasedScore> findByUserIdAndScoreId(Long userId, Long scoreId);
    boolean existsByUserIdAndScoreId(Long userId, Long scoreId);
    void deleteByUserIdAndScoreId(Long userId, Long scoreId);
}