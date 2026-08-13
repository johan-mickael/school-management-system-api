package com.schoolmanagement.promotion.application.command.assignstudenttopromotion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class AssignStudentToPromotionHandler {
  private final PromotionRepository promotions;
  private final StudentRepository students;

  public AssignStudentToPromotionHandler(PromotionRepository promotions, StudentRepository students) {
    this.promotions = promotions;
    this.students = students;
  }

  @Transactional
  public StudentView handle(AssignStudentToPromotionCommand command) {
    PromotionId promotionId = PromotionId.of(command.promotionId());
    Promotion promotion = promotions.getById(promotionId);
    Student student = students.getById(StudentId.of(command.studentId()));

    promotion.admit();
    student.assignToPromotion(promotionId);

    promotions.save(promotion);
    students.save(student);

    return StudentView.from(student);
  }
}
