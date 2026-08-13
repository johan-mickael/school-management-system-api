package com.schoolmanagement.examination.domain;

import java.util.List;
import java.util.Optional;

import com.schoolmanagement.examination.domain.exception.AttemptNotFound;
import com.schoolmanagement.student.domain.StudentId;

public interface ExamAttemptRepository {
    void save(ExamAttempt attempt);

    /**
     * @throws AttemptNotFound
     */
    ExamAttempt getById(AttemptId id);

    Optional<ExamAttempt> findByExamIdAndStudentId(ExamId examId, StudentId studentId);

    List<ExamAttempt> findByExamId(ExamId examId);
}
