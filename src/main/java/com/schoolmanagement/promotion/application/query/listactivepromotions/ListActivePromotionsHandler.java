package com.schoolmanagement.promotion.application.query.listactivepromotions;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.PromotionRepository;

@Service
public class ListActivePromotionsHandler {
  private final PromotionRepository promotions;

  public ListActivePromotionsHandler(PromotionRepository promotions) {
    this.promotions = promotions;
  }

  @Transactional(readOnly = true)
  public List<PromotionView> handle(ListActivePromotionsQuery query) {
    return promotions.findActive().stream()
        .map(PromotionView::from)
        .toList();
  }
}
