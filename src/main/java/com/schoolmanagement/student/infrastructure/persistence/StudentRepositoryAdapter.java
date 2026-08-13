package com.schoolmanagement.student.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.StudentStatus;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

@Repository
public class StudentRepositoryAdapter implements StudentRepository {
  private final StudentJpaRepository studentRepository;
  private final StudentMapper studentMapper;

  public StudentRepositoryAdapter(StudentJpaRepository jpa, StudentMapper mapper) {
    this.studentRepository = jpa;
    this.studentMapper = mapper;
  }

  @Override
  public void save(Student student) {
    studentRepository.save(studentMapper.toEntity(student));
  }

  @Override
  public Student getById(StudentId id) {
    return studentRepository.findById(id.value())
        .map(studentMapper::toDomain)
        .orElseThrow(() -> new StudentNotFound(id));
  }

  @Override
  public List<Student> findByPromotionId(PromotionId promotionId) {
    return studentRepository.findByPromotionId(promotionId.value()).stream()
        .map(studentMapper::toDomain)
        .toList();
  }

  @Override
  public List<Student> findActiveByPromotionId(PromotionId promotionId) {
    return studentRepository.findByPromotionIdAndStatus(promotionId.value(), StudentStatus.ENROLLED).stream()
        .map(studentMapper::toDomain)
        .toList();
  }
}
