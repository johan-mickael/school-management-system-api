package com.schoolmanagement.course.application.command.assignteachertocourse;

public record AssignTeacherToCourseCommand(
    String courseId,
    String teacherId) {
}
