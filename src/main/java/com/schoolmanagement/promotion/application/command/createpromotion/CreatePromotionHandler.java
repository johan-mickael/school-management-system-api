package com.schoolmanagement.promotion.application.command.createpromotion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;

@Service
public class CreatePromotionHandler {
  private final PromotionRepository promotions;

  public CreatePromotionHandler(PromotionRepository promotions) {
    this.promotions = promotions;
  }

  @Transactional
  public PromotionView handle(CreatePromotionCommand command) {
    Promotion promotion = Promotion.create(
        PromotionId.generate(),
        new PromotionName(command.name()),
        new AcademicYear(command.academicYear()),
        new Capacity(command.capacity()));

    promotions.save(promotion);

    return PromotionView.from(promotion);
  }
}
