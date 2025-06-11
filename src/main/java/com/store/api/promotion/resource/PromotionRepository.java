package com.store.api.promotion.resource;

import com.store.api.promotion.model.Promotion;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PromotionRepository {

    private final PromotionMapper promotionMapper;

    public PromotionRepository(PromotionMapper promotionMapper) {
        this.promotionMapper = promotionMapper;
    }

    public Promotion findById(Long promotionId) {
        return promotionMapper.selectById(promotionId);
    }

    public boolean isPromotionPeriod(Long now) {
        List<Promotion> promotions = promotionMapper.selectAllCurrentPromotions(now);
        return !promotions.isEmpty();
    }
}
