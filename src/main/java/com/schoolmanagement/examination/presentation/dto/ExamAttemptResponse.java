package com.schoolmanagement.examination.presentation.dto;

import java.util.List;

import com.schoolmanagement.examination.application.view.ExamAttemptView;

public record ExamAttemptResponse(
    String id,
    String examId,
    String studentId,
    String status,
    int integrityScore,
    List<IntegrityEventResponse> events) {

  public static ExamAttemptResponse from(ExamAttemptView v) {
    return new ExamAttemptResponse(
        v.id(),
        v.examId(),
        v.studentId(),
        v.status(),
        v.integrityScore(),
        v.events().stream().map(IntegrityEventResponse::from).toList());
  }
}
