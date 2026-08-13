package com.schoolmanagement.course.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.exception.CourseNotFound;
import com.schoolmanagement.promotion.domain.PromotionId;

@Repository
public class CourseRepositoryAdapter implements CourseRepository {
  private final CourseJpaRepository courseRepository;
  private final CourseMapper courseMapper;

  public CourseRepositoryAdapter(CourseJpaRepository jpa, CourseMapper mapper) {
    this.courseRepository = jpa;
    this.courseMapper = mapper;
  }

  @Override
  public void save(Course course) {
    courseRepository.save(courseMapper.toEntity(course));
  }

  @Override
  public Course getById(CourseId id) {
    return courseRepository.findById(id.value())
        .map(courseMapper::toDomain)
        .orElseThrow(() -> new CourseNotFound(id));
  }

  @Override
  public List<Course> findByPromotionId(PromotionId promotionId) {
    return courseRepository.findByPromotionId(promotionId.value()).stream()
        .map(courseMapper::toDomain)
        .toList();
  }
}
