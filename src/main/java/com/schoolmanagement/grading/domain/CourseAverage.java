package com.schoolmanagement.grading.domain;

import com.schoolmanagement.course.domain.CourseId;

public record CourseAverage(CourseId courseId, double average) {
}
