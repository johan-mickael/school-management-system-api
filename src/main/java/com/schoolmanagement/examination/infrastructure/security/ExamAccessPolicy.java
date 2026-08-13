package com.schoolmanagement.examination.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.iam.infrastructure.security.CurrentTeacherResolver;

@Component
public class ExamAccessPolicy {
  private final ExamRepository exams;
  private final CourseRepository courses;
  private final CurrentTeacherResolver currentTeacher;

  public ExamAccessPolicy(ExamRepository exams, CourseRepository courses, CurrentTeacherResolver currentTeacher) {
    this.exams = exams;
    this.courses = courses;
    this.currentTeacher = currentTeacher;
  }

  public boolean canManage(String examId, Authentication authentication) {
    Exam exam = exams.getById(ExamId.of(examId));
    Course course = courses.getById(exam.courseId());
    return currentTeacher.resolve(authentication)
        .map(course::isTaughtBy)
        .orElse(false);
  }
}
