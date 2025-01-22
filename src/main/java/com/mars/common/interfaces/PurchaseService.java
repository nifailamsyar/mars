package com.mars.common.interfaces;

import com.mars.common.entity.PurchasedScore;
import java.util.List;

public interface PurchaseService {
    void purchaseScore(String username, Long scoreId);
    List<PurchasedScore> getUserPurchases(String username);
    void deletePurchase(String username, Long scoreId);
    boolean hasPurchased(String username, Long scoreId);
}