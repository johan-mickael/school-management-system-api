package com.schoolmanagement.promotion.application.query.getpromotion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;

@Service
public class GetPromotionHandler {
  private final PromotionRepository promotions;

  public GetPromotionHandler(PromotionRepository promotions) {
    this.promotions = promotions;
  }

  @Transactional(readOnly = true)
  public PromotionView handle(GetPromotionQuery query) {
    Promotion foundPromotion = promotions.getById(PromotionId.of(query.promotionId()));

    return PromotionView.from(foundPromotion);
  }
}
