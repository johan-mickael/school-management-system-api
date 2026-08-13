package com.schoolmanagement.examination.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamJpaRepository extends JpaRepository<ExamEntity, UUID> {
  List<ExamEntity> findByPromotionId(UUID promotionId);
}
