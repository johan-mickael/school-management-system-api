package com.schoolmanagement.grading.application.query.getpromotionrankings;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.grading.application.view.PromotionRankingEntryView;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class GetPromotionRankingsHandler {
  private final GradeStatisticsRepository gradeStatistics;
  private final StudentRepository students;
  private final PromotionRepository promotions;

  public GetPromotionRankingsHandler(
      GradeStatisticsRepository gradeStatistics,
      StudentRepository students,
      PromotionRepository promotions) {
    this.gradeStatistics = gradeStatistics;
    this.students = students;
    this.promotions = promotions;
  }

  @Transactional(readOnly = true)
  public List<PromotionRankingEntryView> handle(GetPromotionRankingsQuery query) {
    PromotionId promotionId = PromotionId.of(query.promotionId());
    promotions.getById(promotionId);

    record StudentAverage(Student student, double average) {}

    List<StudentAverage> ranked = students.findByPromotionId(promotionId).stream()
        .filter(student -> !student.isArchived())
        .map(student -> new StudentAverage(
            student,
            gradeStatistics.overallAverageForStudent(student.id()).orElse(0.0)))
        .sorted(Comparator.comparingDouble(StudentAverage::average).reversed())
        .toList();

    return IntStream.range(0, ranked.size())
        .mapToObj(i -> new PromotionRankingEntryView(
            ranked.get(i).student().id().toString(), ranked.get(i).average(), i + 1))
        .toList();
  }
}
