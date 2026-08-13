package com.schoolmanagement.attendance.domain;

import java.util.Optional;

import com.schoolmanagement.student.domain.StudentId;

public interface AttendanceStatisticsRepository {
    Optional<Double> attendanceRateForStudent(StudentId studentId);
}
