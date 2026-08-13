package com.schoolmanagement.attendance.domain;

import java.util.List;
import java.util.Optional;

import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.student.domain.StudentId;

public interface AttendanceRecordRepository {
    void save(AttendanceRecord record);

    Optional<AttendanceRecord> findBySessionIdAndStudentId(SessionId sessionId, StudentId studentId);

    List<AttendanceRecord> findBySessionId(SessionId sessionId);

    List<AttendanceRecord> findByStudentId(StudentId studentId);
}
