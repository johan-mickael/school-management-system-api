package com.schoolmanagement.course.application.view;

import com.schoolmanagement.course.domain.Course;

public record CourseView(
    String id,
    String code,
    String title,
    double coefficient,
    String promotionId,
    String teacherId) {

    public static CourseView from(Course c) {
        return new CourseView(
                c.id().toString(),
                c.code().value(),
                c.title().value(),
                c.coefficient().value(),
                c.promotionId().toString(),
                c.teacherId() == null ? null : c.teacherId().toString());
    }
}
