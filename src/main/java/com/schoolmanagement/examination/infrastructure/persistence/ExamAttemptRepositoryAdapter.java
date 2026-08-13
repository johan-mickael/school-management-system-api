package com.schoolmanagement.examination.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.exception.AttemptNotFound;
import com.schoolmanagement.student.domain.StudentId;

@Repository
public class ExamAttemptRepositoryAdapter implements ExamAttemptRepository {
  private final ExamAttemptJpaRepository attemptRepository;
  private final ExamAttemptMapper attemptMapper;

  public ExamAttemptRepositoryAdapter(ExamAttemptJpaRepository jpa, ExamAttemptMapper mapper) {
    this.attemptRepository = jpa;
    this.attemptMapper = mapper;
  }

  @Override
  public void save(ExamAttempt attempt) {
    attemptRepository.save(attemptMapper.toEntity(attempt));
  }

  @Override
  public ExamAttempt getById(AttemptId id) {
    return attemptRepository.findById(id.value())
        .map(attemptMapper::toDomain)
        .orElseThrow(() -> new AttemptNotFound(id));
  }

  @Override
  public Optional<ExamAttempt> findByExamIdAndStudentId(ExamId examId, StudentId studentId) {
    return attemptRepository.findByExamIdAndStudentId(examId.value(), studentId.value())
        .map(attemptMapper::toDomain);
  }

  @Override
  public List<ExamAttempt> findByExamId(ExamId examId) {
    return attemptRepository.findByExamId(examId.value()).stream()
        .map(attemptMapper::toDomain)
        .toList();
  }
}
