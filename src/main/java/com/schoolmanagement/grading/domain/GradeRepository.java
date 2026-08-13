package com.schoolmanagement.grading.domain;

import java.util.List;

import com.schoolmanagement.grading.domain.exception.GradeNotFound;
import com.schoolmanagement.student.domain.StudentId;

public interface GradeRepository {
    void save(Grade grade);

    /**
     * @throws GradeNotFound
     */
    Grade getById(GradeId id);

    List<Grade> findByStudentId(StudentId studentId);
}
