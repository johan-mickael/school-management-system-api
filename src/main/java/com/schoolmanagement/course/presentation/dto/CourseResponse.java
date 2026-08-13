package com.schoolmanagement.course.presentation.dto;

import com.schoolmanagement.course.application.view.CourseView;

public record CourseResponse(
    String id,
    String code,
    String title,
    double coefficient,
    String promotionId,
    String teacherId) {

  public static CourseResponse from(CourseView v) {
    return new CourseResponse(
        v.id(),
        v.code(),
        v.title(),
        v.coefficient(),
        v.promotionId(),
        v.teacherId());
  }
}
