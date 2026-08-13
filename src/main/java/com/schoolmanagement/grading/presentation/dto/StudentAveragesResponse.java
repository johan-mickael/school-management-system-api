package com.schoolmanagement.grading.presentation.dto;

import java.util.List;

import com.schoolmanagement.grading.application.view.StudentAveragesView;

public record StudentAveragesResponse(
    String studentId,
    List<CourseAverageResponse> courseAverages,
    Double overallAverage) {

  public static StudentAveragesResponse from(StudentAveragesView v) {
    return new StudentAveragesResponse(
        v.studentId(),
        v.courseAverages().stream().map(CourseAverageResponse::from).toList(),
        v.overallAverage());
  }
}
