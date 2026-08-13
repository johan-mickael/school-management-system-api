package com.schoolmanagement.grading.infrastructure.persistence;

import java.util.UUID;

public record CourseAverageRow(UUID courseId, Double average) {
}
