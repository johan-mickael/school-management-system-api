package com.schoolmanagement.attendance.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordJpaRepository extends JpaRepository<AttendanceRecordEntity, UUID> {
  Optional<AttendanceRecordEntity> findBySessionIdAndStudentId(UUID sessionId, UUID studentId);

  List<AttendanceRecordEntity> findBySessionId(UUID sessionId);

  List<AttendanceRecordEntity> findByStudentId(UUID studentId);
}
