package com.schoolmanagement.examination.application.command.scheduleexam;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.shared.domain.TimeWindow;

@Service
public class ScheduleExamHandler {
  private final ExamRepository exams;
  private final CourseRepository courses;
  private final PromotionRepository promotions;

  public ScheduleExamHandler(ExamRepository exams, CourseRepository courses, PromotionRepository promotions) {
    this.exams = exams;
    this.courses = courses;
    this.promotions = promotions;
  }

  @Transactional
  public ExamView handle(ScheduleExamCommand command) {
    CourseId courseId = CourseId.of(command.courseId());
    courses.getById(courseId);
    PromotionId promotionId = PromotionId.of(command.promotionId());
    promotions.getById(promotionId);

    Exam exam = Exam.schedule(
        ExamId.generate(),
        courseId,
        promotionId,
        new TimeWindow(command.start(), command.end()));

    exams.save(exam);

    return ExamView.from(exam);
  }
}
