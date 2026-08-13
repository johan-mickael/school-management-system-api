package com.schoolmanagement.promotion.application.query.listpromotionstudents;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class ListPromotionStudentsHandler {
  private final PromotionRepository promotions;
  private final StudentRepository students;

  public ListPromotionStudentsHandler(PromotionRepository promotions, StudentRepository students) {
    this.promotions = promotions;
    this.students = students;
  }

  @Transactional(readOnly = true)
  public List<StudentView> handle(ListPromotionStudentsQuery query) {
    PromotionId id = PromotionId.of(query.promotionId());
    promotions.getById(id);

    return students.findByPromotionId(id).stream()
        .filter(s -> !s.isArchived())
        .map(StudentView::from)
        .toList();
  }
}
