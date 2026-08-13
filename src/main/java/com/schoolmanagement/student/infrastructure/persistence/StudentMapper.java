package com.schoolmanagement.student.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.student.domain.EmailAddress;
import com.schoolmanagement.student.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;

@Component
public class StudentMapper {

  public StudentEntity toEntity(Student s) {
    return new StudentEntity(
        s.id().value(),
        s.number().value(),
        s.name().firstName(),
        s.name().lastName(),
        s.email().value(),
        s.status(),
        s.enrolledAt(),
        s.promotionId() == null ? null : s.promotionId().value());
  }

  public Student toDomain(StudentEntity e) {
    return Student.reconstitute(
        new StudentId(e.getId()),
        new StudentNumber(e.getNumber()),
        new FullName(e.getFirstName(), e.getLastName()),
        new EmailAddress(e.getEmail()),
        e.getStatus(),
        e.getEnrolledAt(),
        e.getPromotionId() == null ? null : new PromotionId(e.getPromotionId()));
  }
}
