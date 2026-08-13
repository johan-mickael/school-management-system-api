package com.schoolmanagement.course.application.command.createcourse;

public record CreateCourseCommand(
    String code,
    String title,
    double coefficient,
    String promotionId,
    String teacherId) {
}
