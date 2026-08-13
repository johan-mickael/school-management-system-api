package com.schoolmanagement.examination.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.examination.domain.exception.ExamNotFound;
import com.schoolmanagement.promotion.domain.PromotionId;

@Repository
public class ExamRepositoryAdapter implements ExamRepository {
  private final ExamJpaRepository examRepository;
  private final ExamMapper examMapper;

  public ExamRepositoryAdapter(ExamJpaRepository jpa, ExamMapper mapper) {
    this.examRepository = jpa;
    this.examMapper = mapper;
  }

  @Override
  public void save(Exam exam) {
    examRepository.save(examMapper.toEntity(exam));
  }

  @Override
  public Exam getById(ExamId id) {
    return examRepository.findById(id.value())
        .map(examMapper::toDomain)
        .orElseThrow(() -> new ExamNotFound(id));
  }

  @Override
  public List<Exam> findByPromotionId(PromotionId promotionId) {
    return examRepository.findByPromotionId(promotionId.value()).stream()
        .map(examMapper::toDomain)
        .toList();
  }
}
