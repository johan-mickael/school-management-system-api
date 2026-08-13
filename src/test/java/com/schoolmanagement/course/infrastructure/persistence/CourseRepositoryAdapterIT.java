package com.schoolmanagement.course.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.course.domain.exception.CourseNotFound;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.shared.AbstractIntegrationTest;

@Transactional
class CourseRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  @Test
  void saves_and_retrieves_a_course() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);

    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS101"), new CourseTitle("Algorithms"),
        new Coefficient(3.0), promotion.id(), null);

    courses.save(course);

    Course found = courses.getById(course.id());

    assertThat(found.id()).isEqualTo(course.id());
    assertThat(found.code()).isEqualTo(course.code());
    assertThat(found.title()).isEqualTo(course.title());
    assertThat(found.coefficient()).isEqualTo(course.coefficient());
    assertThat(found.promotionId()).isEqualTo(promotion.id());
    assertThat(found.teacherId()).isNull();
  }

  @Test
  void throws_when_course_not_found() {
    assertThatThrownBy(() -> courses.getById(CourseId.generate()))
        .isInstanceOf(CourseNotFound.class);
  }

  @Test
  void finds_courses_by_promotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS102"), new CourseTitle("Databases"),
        new Coefficient(2.0), promotion.id(), null);
    courses.save(course);

    assertThat(courses.findByPromotionId(promotion.id())).containsExactly(course);
  }
}
