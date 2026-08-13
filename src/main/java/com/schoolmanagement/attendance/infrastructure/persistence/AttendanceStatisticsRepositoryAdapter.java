package com.schoolmanagement.attendance.infrastructure.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.attendance.domain.AttendanceStatisticsRepository;
import com.schoolmanagement.attendance.domain.AttendanceStatus;
import com.schoolmanagement.student.domain.StudentId;

@Repository
public class AttendanceStatisticsRepositoryAdapter implements AttendanceStatisticsRepository {
  private final AttendanceRecordJpaRepository attendanceRecordRepository;

  public AttendanceStatisticsRepositoryAdapter(AttendanceRecordJpaRepository attendanceRecordRepository) {
    this.attendanceRecordRepository = attendanceRecordRepository;
  }

  @Override
  public Optional<Double> attendanceRateForStudent(StudentId studentId) {
    long total = attendanceRecordRepository.countByStudentId(studentId.value());
    if (total == 0) {
      return Optional.empty();
    }
    long absent = attendanceRecordRepository.countByStudentIdAndStatus(studentId.value(), AttendanceStatus.ABSENT);
    return Optional.of((double) (total - absent) / total);
  }
}
