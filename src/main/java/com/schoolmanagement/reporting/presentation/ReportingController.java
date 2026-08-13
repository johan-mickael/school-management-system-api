package com.schoolmanagement.reporting.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.reporting.application.query.getatriskstudents.GetAtRiskStudentsHandler;
import com.schoolmanagement.reporting.application.query.getatriskstudents.GetAtRiskStudentsQuery;
import com.schoolmanagement.reporting.application.query.getpromotionsummary.GetPromotionSummaryHandler;
import com.schoolmanagement.reporting.application.query.getpromotionsummary.GetPromotionSummaryQuery;
import com.schoolmanagement.reporting.application.query.getstudentsummary.GetStudentSummaryHandler;
import com.schoolmanagement.reporting.application.query.getstudentsummary.GetStudentSummaryQuery;
import com.schoolmanagement.reporting.presentation.dto.AtRiskStudentResponse;
import com.schoolmanagement.reporting.presentation.dto.PromotionSummaryResponse;
import com.schoolmanagement.reporting.presentation.dto.StudentSummaryResponse;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Reporting")
public class ReportingController {

  private final GetStudentSummaryHandler studentSummary;
  private final GetPromotionSummaryHandler promotionSummary;
  private final GetAtRiskStudentsHandler atRiskStudents;

  public ReportingController(
      GetStudentSummaryHandler studentSummary,
      GetPromotionSummaryHandler promotionSummary,
      GetAtRiskStudentsHandler atRiskStudents) {
    this.studentSummary = studentSummary;
    this.promotionSummary = promotionSummary;
    this.atRiskStudents = atRiskStudents;
  }

  @GetMapping("/api/v1/reporting/students/{studentId}/summary")
  public StudentSummaryResponse getStudentSummary(@PathVariable String studentId) {
    return StudentSummaryResponse.from(studentSummary.handle(new GetStudentSummaryQuery(studentId)));
  }

  @GetMapping("/api/v1/reporting/promotions/{promotionId}/summary")
  public PromotionSummaryResponse getPromotionSummary(@PathVariable String promotionId) {
    return PromotionSummaryResponse.from(promotionSummary.handle(new GetPromotionSummaryQuery(promotionId)));
  }

  @GetMapping("/api/v1/reporting/promotions/{promotionId}/at-risk")
  public List<AtRiskStudentResponse> getAtRiskStudents(@PathVariable String promotionId) {
    return atRiskStudents.handle(new GetAtRiskStudentsQuery(promotionId)).stream()
        .map(AtRiskStudentResponse::from)
        .toList();
  }
}
