package com.schoolmanagement.course.application.command.createcourse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.application.view.CourseView;
import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class CreateCourseHandler {
  private final CourseRepository courses;
  private final PromotionRepository promotions;
  private final TeacherRepository teachers;

  public CreateCourseHandler(CourseRepository courses, PromotionRepository promotions, TeacherRepository teachers) {
    this.courses = courses;
    this.promotions = promotions;
    this.teachers = teachers;
  }

  @Transactional
  public CourseView handle(CreateCourseCommand command) {
    PromotionId promotionId = PromotionId.of(command.promotionId());
    promotions.getById(promotionId);

    TeacherId teacherId = null;
    if (command.teacherId() != null && !command.teacherId().isBlank()) {
      teacherId = TeacherId.of(command.teacherId());
      teachers.getById(teacherId);
    }

    Course course = Course.create(
        CourseId.generate(),
        new CourseCode(command.code()),
        new CourseTitle(command.title()),
        new Coefficient(command.coefficient()),
        promotionId,
        teacherId);

    courses.save(course);

    return CourseView.from(course);
  }
}
