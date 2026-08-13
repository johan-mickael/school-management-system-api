package com.schoolmanagement.examination.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamAttemptJpaRepository extends JpaRepository<ExamAttemptEntity, UUID> {
  Optional<ExamAttemptEntity> findByExamIdAndStudentId(UUID examId, UUID studentId);

  List<ExamAttemptEntity> findByExamId(UUID examId);
}
