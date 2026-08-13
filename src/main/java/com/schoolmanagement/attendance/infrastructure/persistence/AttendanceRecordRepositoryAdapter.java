package com.schoolmanagement.attendance.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.student.domain.StudentId;

@Repository
public class AttendanceRecordRepositoryAdapter implements AttendanceRecordRepository {
  private final AttendanceRecordJpaRepository attendanceRecordRepository;
  private final AttendanceRecordMapper attendanceRecordMapper;

  public AttendanceRecordRepositoryAdapter(AttendanceRecordJpaRepository jpa, AttendanceRecordMapper mapper) {
    this.attendanceRecordRepository = jpa;
    this.attendanceRecordMapper = mapper;
  }

  @Override
  public void save(AttendanceRecord record) {
    attendanceRecordRepository.save(attendanceRecordMapper.toEntity(record));
  }

  @Override
  public Optional<AttendanceRecord> findBySessionIdAndStudentId(SessionId sessionId, StudentId studentId) {
    return attendanceRecordRepository.findBySessionIdAndStudentId(sessionId.value(), studentId.value())
        .map(attendanceRecordMapper::toDomain);
  }

  @Override
  public List<AttendanceRecord> findBySessionId(SessionId sessionId) {
    return attendanceRecordRepository.findBySessionId(sessionId.value()).stream()
        .map(attendanceRecordMapper::toDomain)
        .toList();
  }

  @Override
  public List<AttendanceRecord> findByStudentId(StudentId studentId) {
    return attendanceRecordRepository.findByStudentId(studentId.value()).stream()
        .map(attendanceRecordMapper::toDomain)
        .toList();
  }
}
