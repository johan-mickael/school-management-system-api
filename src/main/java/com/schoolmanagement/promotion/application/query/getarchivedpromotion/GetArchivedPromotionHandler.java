package com.schoolmanagement.promotion.application.query.getarchivedpromotion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;

@Service
public class GetArchivedPromotionHandler {
  private final PromotionRepository promotions;

  public GetArchivedPromotionHandler(PromotionRepository promotions) {
    this.promotions = promotions;
  }

  /**
   * @throws PromotionNotFound if the promotion does not exist, or exists but is not archived
   */
  @Transactional(readOnly = true)
  public PromotionView handle(GetArchivedPromotionQuery query) {
    PromotionId promotionId = PromotionId.of(query.promotionId());
    Promotion promotion = promotions.getById(promotionId);
    if (!promotion.isArchived()) {
      throw new PromotionNotFound(promotionId);
    }
    return PromotionView.from(promotion);
  }
}
