package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.StudentId;

public final class AttemptAlreadyExists extends DomainException {
    public AttemptAlreadyExists(ExamId examId, StudentId studentId) {
        super("Student '%s' already has an attempt for exam '%s'".formatted(studentId, examId));
    }
}
