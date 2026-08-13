package com.schoolmanagement.grading.domain;

import java.util.Objects;
import java.util.UUID;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.student.domain.StudentId;

public final class Grade {
    private final GradeId id;
    private final StudentId studentId;
    private final CourseId courseId;
    // Raw UUID, not a value object: the examination context (and its ExamId) doesn't exist yet.
    private final UUID examId;
    private Score score;
    private final Coefficient coefficient;

    private Grade(GradeId id,
            StudentId studentId,
            CourseId courseId,
            UUID examId,
            Score score,
            Coefficient coefficient) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.examId = examId;
        this.score = score;
        this.coefficient = coefficient;
    }

    public static Grade record(GradeId id,
            StudentId studentId,
            CourseId courseId,
            UUID examId,
            Score score,
            Coefficient coefficient) {
        return new Grade(id, studentId, courseId, examId, score, coefficient);
    }

    public static Grade reconstitute(GradeId id,
            StudentId studentId,
            CourseId courseId,
            UUID examId,
            Score score,
            Coefficient coefficient) {
        return new Grade(id, studentId, courseId, examId, score, coefficient);
    }

    public void correct(Score newScore) {
        this.score = newScore;
    }

    public GradeId id() {
        return id;
    }

    public StudentId studentId() {
        return studentId;
    }

    public CourseId courseId() {
        return courseId;
    }

    public UUID examId() {
        return examId;
    }

    public Score score() {
        return score;
    }

    public Coefficient coefficient() {
        return coefficient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Grade other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
