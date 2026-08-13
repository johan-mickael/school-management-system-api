package com.schoolmanagement.student.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.schoolmanagement.student.domain.StudentStatus;

public interface StudentJpaRepository extends JpaRepository<StudentEntity, UUID> {
  List<StudentEntity> findByPromotionId(UUID promotionId);

  List<StudentEntity> findByPromotionIdAndStatus(UUID promotionId, StudentStatus status);
}
