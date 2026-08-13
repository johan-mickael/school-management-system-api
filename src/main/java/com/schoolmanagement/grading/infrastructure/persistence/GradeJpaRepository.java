package com.schoolmanagement.grading.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GradeJpaRepository extends JpaRepository<GradeEntity, UUID> {
  List<GradeEntity> findByStudentId(UUID studentId);

  @Query("""
      SELECT new com.schoolmanagement.grading.infrastructure.persistence.CourseAverageRow(
          g.courseId, SUM(g.score * g.coefficient) / SUM(g.coefficient))
      FROM GradeEntity g
      WHERE g.studentId = :studentId
      GROUP BY g.courseId
      """)
  List<CourseAverageRow> courseAveragesByStudentId(@Param("studentId") UUID studentId);

  @Query("""
      SELECT SUM(g.score * g.coefficient) / SUM(g.coefficient)
      FROM GradeEntity g
      WHERE g.studentId = :studentId
      """)
  Double overallAverageByStudentId(@Param("studentId") UUID studentId);
}
