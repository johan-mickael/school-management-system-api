package com.schoolmanagement.course.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseJpaRepository extends JpaRepository<CourseEntity, UUID> {
  List<CourseEntity> findByPromotionId(UUID promotionId);
}
