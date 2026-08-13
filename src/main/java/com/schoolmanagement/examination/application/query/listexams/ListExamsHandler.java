package com.schoolmanagement.examination.application.query.listexams;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.promotion.domain.PromotionId;

@Service
public class ListExamsHandler {
  private final ExamRepository exams;

  public ListExamsHandler(ExamRepository exams) {
    this.exams = exams;
  }

  @Transactional(readOnly = true)
  public List<ExamView> handle(ListExamsQuery query) {
    return exams.findByPromotionId(PromotionId.of(query.promotionId())).stream()
        .map(ExamView::from)
        .toList();
  }
}
