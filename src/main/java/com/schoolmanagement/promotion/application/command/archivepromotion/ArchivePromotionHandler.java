package com.schoolmanagement.promotion.application.command.archivepromotion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class ArchivePromotionHandler {
  private final PromotionRepository promotions;
  private final StudentRepository students;

  public ArchivePromotionHandler(PromotionRepository promotions, StudentRepository students) {
    this.promotions = promotions;
    this.students = students;
  }

  @Transactional
  public PromotionView handle(ArchivePromotionCommand command) {
    PromotionId promotionId = PromotionId.of(command.promotionId());
    Promotion promotion = promotions.getById(promotionId);

    promotion.archive();
    promotions.save(promotion);

    for (Student student : students.findActiveByPromotionId(promotionId)) {
      student.archive();
      students.save(student);
    }

    return PromotionView.from(promotion);
  }
}
