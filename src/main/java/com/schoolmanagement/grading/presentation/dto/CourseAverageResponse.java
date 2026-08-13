package com.schoolmanagement.grading.presentation.dto;

import com.schoolmanagement.grading.application.view.CourseAverageView;

public record CourseAverageResponse(String courseId, double average) {

  public static CourseAverageResponse from(CourseAverageView v) {
    return new CourseAverageResponse(v.courseId(), v.average());
  }
}
