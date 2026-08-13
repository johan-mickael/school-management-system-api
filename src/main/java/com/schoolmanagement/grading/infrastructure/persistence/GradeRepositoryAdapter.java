package com.schoolmanagement.grading.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.exception.GradeNotFound;
import com.schoolmanagement.student.domain.StudentId;

@Repository
public class GradeRepositoryAdapter implements GradeRepository {
  private final GradeJpaRepository gradeRepository;
  private final GradeMapper gradeMapper;

  public GradeRepositoryAdapter(GradeJpaRepository jpa, GradeMapper mapper) {
    this.gradeRepository = jpa;
    this.gradeMapper = mapper;
  }

  @Override
  public void save(Grade grade) {
    gradeRepository.save(gradeMapper.toEntity(grade));
  }

  @Override
  public Grade getById(GradeId id) {
    return gradeRepository.findById(id.value())
        .map(gradeMapper::toDomain)
        .orElseThrow(() -> new GradeNotFound(id));
  }

  @Override
  public List<Grade> findByStudentId(StudentId studentId) {
    return gradeRepository.findByStudentId(studentId.value()).stream()
        .map(gradeMapper::toDomain)
        .toList();
  }
}
