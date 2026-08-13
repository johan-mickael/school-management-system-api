package com.schoolmanagement.course.application.query.listcoursesbypromotion;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.application.view.CourseView;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.promotion.domain.PromotionId;

@Service
public class ListCoursesByPromotionHandler {
  private final CourseRepository courses;

  public ListCoursesByPromotionHandler(CourseRepository courses) {
    this.courses = courses;
  }

  @Transactional(readOnly = true)
  public List<CourseView> handle(ListCoursesByPromotionQuery query) {
    return courses.findByPromotionId(PromotionId.of(query.promotionId())).stream()
        .map(CourseView::from)
        .toList();
  }
}
