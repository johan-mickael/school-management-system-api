package com.schoolmanagement.grading.application.view;

import java.util.List;

public record StudentAveragesView(
    String studentId,
    List<CourseAverageView> courseAverages,
    Double overallAverage) {
}
