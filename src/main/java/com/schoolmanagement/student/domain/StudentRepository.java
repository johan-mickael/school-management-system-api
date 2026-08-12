package com.schoolmanagement.student.domain;

import com.schoolmanagement.student.domain.exception.StudentNotFound;

public interface StudentRepository {
    void save(Student student);

    /**
     * @throws StudentNotFound
     */
    Student getById(StudentId id);
}