package com.schoolmanagement.grading.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.grading.application.command.correctgrade.CorrectGradeCommand;
import com.schoolmanagement.grading.application.command.correctgrade.CorrectGradeHandler;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeCommand;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeHandler;
import com.schoolmanagement.grading.application.query.getpromotionrankings.GetPromotionRankingsHandler;
import com.schoolmanagement.grading.application.query.getpromotionrankings.GetPromotionRankingsQuery;
import com.schoolmanagement.grading.application.query.getstudentaverages.GetStudentAveragesHandler;
import com.schoolmanagement.grading.application.query.getstudentaverages.GetStudentAveragesQuery;
import com.schoolmanagement.grading.application.query.getstudentgrades.GetStudentGradesHandler;
import com.schoolmanagement.grading.application.query.getstudentgrades.GetStudentGradesQuery;
import com.schoolmanagement.grading.application.view.GradeView;
import com.schoolmanagement.grading.presentation.dto.CorrectGradeRequest;
import com.schoolmanagement.grading.presentation.dto.GradeResponse;
import com.schoolmanagement.grading.presentation.dto.PromotionRankingEntryResponse;
import com.schoolmanagement.grading.presentation.dto.RecordGradeRequest;
import com.schoolmanagement.grading.presentation.dto.StudentAveragesResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Grades")
public class GradeController {

  private final RecordGradeHandler record;
  private final CorrectGradeHandler correct;
  private final GetStudentGradesHandler studentGrades;
  private final GetStudentAveragesHandler studentAverages;
  private final GetPromotionRankingsHandler promotionRankings;

  public GradeController(
      RecordGradeHandler record,
      CorrectGradeHandler correct,
      GetStudentGradesHandler studentGrades,
      GetStudentAveragesHandler studentAverages,
      GetPromotionRankingsHandler promotionRankings) {
    this.record = record;
    this.correct = correct;
    this.studentGrades = studentGrades;
    this.studentAverages = studentAverages;
    this.promotionRankings = promotionRankings;
  }

  @PostMapping("/api/v1/courses/{courseId}/grades")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @courseAccessPolicy.canManage(#courseId, authentication))")
  public ResponseEntity<GradeResponse> record(
      @P("courseId") @PathVariable String courseId,
      @Valid @RequestBody RecordGradeRequest request) {
    RecordGradeCommand recordGradeCommand = new RecordGradeCommand(
        courseId,
        request.studentId(),
        request.examId(),
        request.score(),
        request.coefficient());
    GradeView view = record.handle(recordGradeCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/grades/" + view.id()))
        .body(GradeResponse.from(view));
  }

  @GetMapping("/api/v1/students/{studentId}/grades")
  public List<GradeResponse> getStudentGrades(@PathVariable String studentId) {
    return studentGrades.handle(new GetStudentGradesQuery(studentId)).stream()
        .map(GradeResponse::from)
        .toList();
  }

  @PostMapping("/api/v1/grades/{id}/correct")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @gradeAccessPolicy.canCorrect(#id, authentication))")
  public GradeResponse correct(@P("id") @PathVariable String id, @Valid @RequestBody CorrectGradeRequest request) {
    return GradeResponse.from(correct.handle(new CorrectGradeCommand(id, request.score())));
  }

  @GetMapping("/api/v1/students/{studentId}/averages")
  public StudentAveragesResponse getStudentAverages(@PathVariable String studentId) {
    return StudentAveragesResponse.from(studentAverages.handle(new GetStudentAveragesQuery(studentId)));
  }

  @GetMapping("/api/v1/promotions/{promotionId}/rankings")
  public List<PromotionRankingEntryResponse> getPromotionRankings(@PathVariable String promotionId) {
    return promotionRankings.handle(new GetPromotionRankingsQuery(promotionId)).stream()
        .map(PromotionRankingEntryResponse::from)
        .toList();
  }
}
