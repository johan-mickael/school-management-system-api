package com.schoolmanagement.grading.application.view;

import com.schoolmanagement.grading.domain.CourseAverage;

public record CourseAverageView(String courseId, double average) {

    public static CourseAverageView from(CourseAverage a) {
        return new CourseAverageView(a.courseId().toString(), a.average());
    }
}
