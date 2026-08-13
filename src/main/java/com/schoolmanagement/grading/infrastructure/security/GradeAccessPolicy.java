package com.schoolmanagement.grading.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.iam.infrastructure.security.CurrentTeacherResolver;

@Component
public class GradeAccessPolicy {
  private final GradeRepository grades;
  private final CourseRepository courses;
  private final CurrentTeacherResolver currentTeacher;

  public GradeAccessPolicy(GradeRepository grades, CourseRepository courses, CurrentTeacherResolver currentTeacher) {
    this.grades = grades;
    this.courses = courses;
    this.currentTeacher = currentTeacher;
  }

  public boolean canCorrect(String gradeId, Authentication authentication) {
    Grade grade = grades.getById(GradeId.of(gradeId));
    Course course = courses.getById(grade.courseId());
    return currentTeacher.resolve(authentication)
        .map(course::isTaughtBy)
        .orElse(false);
  }
}
