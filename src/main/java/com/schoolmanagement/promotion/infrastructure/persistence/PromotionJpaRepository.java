package com.schoolmanagement.promotion.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schoolmanagement.promotion.domain.PromotionStatus;

public interface PromotionJpaRepository extends JpaRepository<PromotionEntity, UUID> {
  List<PromotionEntity> findByStatus(PromotionStatus status);
}
