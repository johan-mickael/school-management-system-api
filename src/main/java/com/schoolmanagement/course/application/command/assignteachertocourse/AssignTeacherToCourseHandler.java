package com.schoolmanagement.course.application.command.assignteachertocourse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.application.view.CourseView;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class AssignTeacherToCourseHandler {
  private final CourseRepository courses;
  private final TeacherRepository teachers;

  public AssignTeacherToCourseHandler(CourseRepository courses, TeacherRepository teachers) {
    this.courses = courses;
    this.teachers = teachers;
  }

  @Transactional
  public CourseView handle(AssignTeacherToCourseCommand command) {
    Course course = courses.getById(CourseId.of(command.courseId()));
    TeacherId teacherId = TeacherId.of(command.teacherId());
    teachers.getById(teacherId);

    course.assignTeacher(teacherId);
    courses.save(course);

    return CourseView.from(course);
  }
}
