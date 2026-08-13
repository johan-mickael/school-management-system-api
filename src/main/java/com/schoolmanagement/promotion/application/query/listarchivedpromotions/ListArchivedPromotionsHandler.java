package com.schoolmanagement.promotion.application.query.listarchivedpromotions;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.PromotionRepository;

@Service
public class ListArchivedPromotionsHandler {
  private final PromotionRepository promotions;

  public ListArchivedPromotionsHandler(PromotionRepository promotions) {
    this.promotions = promotions;
  }

  @Transactional(readOnly = true)
  public List<PromotionView> handle(ListArchivedPromotionsQuery query) {
    return promotions.findArchived().stream()
        .map(PromotionView::from)
        .toList();
  }
}
