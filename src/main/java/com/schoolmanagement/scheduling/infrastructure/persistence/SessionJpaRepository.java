package com.schoolmanagement.scheduling.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionJpaRepository extends JpaRepository<SessionEntity, UUID> {
  List<SessionEntity> findByPromotionId(UUID promotionId);
}
