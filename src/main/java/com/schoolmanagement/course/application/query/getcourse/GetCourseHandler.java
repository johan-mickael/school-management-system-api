package com.schoolmanagement.course.application.query.getcourse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.application.view.CourseView;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;

@Service
public class GetCourseHandler {
  private final CourseRepository courses;

  public GetCourseHandler(CourseRepository courses) {
    this.courses = courses;
  }

  @Transactional(readOnly = true)
  public CourseView handle(GetCourseQuery query) {
    Course foundCourse = courses.getById(CourseId.of(query.courseId()));

    return CourseView.from(foundCourse);
  }
}
