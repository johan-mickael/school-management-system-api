package com.schoolmanagement.teacher.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;
import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;

@Repository
public class TeacherRepositoryAdapter implements TeacherRepository {
  private final TeacherJpaRepository teacherRepository;
  private final TeacherMapper teacherMapper;

  public TeacherRepositoryAdapter(TeacherJpaRepository jpa, TeacherMapper mapper) {
    this.teacherRepository = jpa;
    this.teacherMapper = mapper;
  }

  @Override
  public void save(Teacher teacher) {
    teacherRepository.save(teacherMapper.toEntity(teacher));
  }

  @Override
  public Teacher getById(TeacherId id) {
    return teacherRepository.findById(id.value())
        .map(teacherMapper::toDomain)
        .orElseThrow(() -> new TeacherNotFound(id));
  }

  @Override
  public List<Teacher> findAll() {
    return teacherRepository.findAll().stream()
        .map(teacherMapper::toDomain)
        .toList();
  }
}
