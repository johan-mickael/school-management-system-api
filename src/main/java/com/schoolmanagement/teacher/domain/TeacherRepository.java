package com.schoolmanagement.teacher.domain;

import java.util.List;

import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;

public interface TeacherRepository {
    void save(Teacher teacher);

    /**
     * @throws TeacherNotFound
     */
    Teacher getById(TeacherId id);

    List<Teacher> findAll();
}
