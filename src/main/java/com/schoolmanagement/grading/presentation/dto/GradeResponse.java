package com.schoolmanagement.grading.presentation.dto;

import com.schoolmanagement.grading.application.view.GradeView;

public record GradeResponse(
    String id,
    String studentId,
    String courseId,
    String examId,
    double score,
    double coefficient) {

  public static GradeResponse from(GradeView v) {
    return new GradeResponse(
        v.id(),
        v.studentId(),
        v.courseId(),
        v.examId(),
        v.score(),
        v.coefficient());
  }
}
