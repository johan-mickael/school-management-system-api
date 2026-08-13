package com.schoolmanagement.course.domain;

import java.util.Objects;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.teacher.domain.TeacherId;

public final class Course {
    private final CourseId id;
    private final CourseCode code;
    private final CourseTitle title;
    private final Coefficient coefficient;
    private final PromotionId promotionId;
    private TeacherId teacherId;

    private Course(CourseId id,
            CourseCode code,
            CourseTitle title,
            Coefficient coefficient,
            PromotionId promotionId,
            TeacherId teacherId) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.coefficient = coefficient;
        this.promotionId = promotionId;
        this.teacherId = teacherId;
    }

    public static Course create(CourseId id,
            CourseCode code,
            CourseTitle title,
            Coefficient coefficient,
            PromotionId promotionId,
            TeacherId teacherId) {
        return new Course(id, code, title, coefficient, promotionId, teacherId);
    }

    public static Course reconstitute(CourseId id,
            CourseCode code,
            CourseTitle title,
            Coefficient coefficient,
            PromotionId promotionId,
            TeacherId teacherId) {
        return new Course(id, code, title, coefficient, promotionId, teacherId);
    }

    public void assignTeacher(TeacherId teacherId) {
        this.teacherId = teacherId;
    }

    public CourseId id() {
        return id;
    }

    public CourseCode code() {
        return code;
    }

    public CourseTitle title() {
        return title;
    }

    public Coefficient coefficient() {
        return coefficient;
    }

    public PromotionId promotionId() {
        return promotionId;
    }

    public TeacherId teacherId() {
        return teacherId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Course other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
