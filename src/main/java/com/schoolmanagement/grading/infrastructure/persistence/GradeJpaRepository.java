package com.schoolmanagement.grading.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeJpaRepository extends JpaRepository<GradeEntity, UUID> {
  List<GradeEntity> findByStudentId(UUID studentId);
}
