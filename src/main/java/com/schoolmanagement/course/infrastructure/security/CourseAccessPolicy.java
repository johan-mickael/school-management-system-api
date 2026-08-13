package com.schoolmanagement.course.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.iam.infrastructure.security.CurrentTeacherResolver;

@Component
public class CourseAccessPolicy {
  private final CourseRepository courses;
  private final CurrentTeacherResolver currentTeacher;

  public CourseAccessPolicy(CourseRepository courses, CurrentTeacherResolver currentTeacher) {
    this.courses = courses;
    this.currentTeacher = currentTeacher;
  }

  public boolean canManage(String courseId, Authentication authentication) {
    Course course = courses.getById(CourseId.of(courseId));
    return currentTeacher.resolve(authentication)
        .map(course::isTaughtBy)
        .orElse(false);
  }
}
