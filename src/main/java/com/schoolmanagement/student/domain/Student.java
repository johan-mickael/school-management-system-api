package com.schoolmanagement.student.domain;

import java.time.Instant;
import java.util.Objects;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.exception.StudentAlreadyArchived;
import com.schoolmanagement.student.domain.exception.StudentAlreadyAssignedToPromotion;

public final class Student {
    private final StudentId id;
    private final StudentNumber number;
    private final FullName name;
    private final EmailAddress email;
    private StudentStatus status;
    private final Instant enrolledAt;
    private PromotionId promotionId;

    private Student(StudentId id,
            StudentNumber number,
            FullName name,
            EmailAddress email,
            StudentStatus status,
            Instant enrolledAt,
            PromotionId promotionId) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.email = email;
        this.status = status;
        this.enrolledAt = enrolledAt;
        this.promotionId = promotionId;
    }

    public static Student enroll(StudentId id,
            StudentNumber number,
            FullName name,
            EmailAddress email,
            Instant enrolledAt) {
        return new Student(id, number, name, email, StudentStatus.ENROLLED, enrolledAt, null);
    }

    public void archive() {
        if (status == StudentStatus.ARCHIVED) {
            throw new StudentAlreadyArchived(id);
        }
        this.status = StudentStatus.ARCHIVED;
    }

    public boolean isArchived() {
        return status == StudentStatus.ARCHIVED;
    }

    /**
     * @throws StudentAlreadyArchived if this student is archived
     */
    public void ensureActive() {
        if (isArchived()) {
            throw new StudentAlreadyArchived(id);
        }
    }

    public void assignToPromotion(PromotionId promotionId) {
        ensureActive();
        if (this.promotionId != null) {
            throw new StudentAlreadyAssignedToPromotion(id);
        }
        this.promotionId = promotionId;
    }

    public static Student reconstitute(StudentId id,
            StudentNumber number,
            FullName name,
            EmailAddress email,
            StudentStatus status,
            Instant enrolledAt,
            PromotionId promotionId) {
        return new Student(id, number, name, email, status, enrolledAt, promotionId);
    }

    public StudentId id() {
        return id;
    }

    public StudentNumber number() {
        return number;
    }

    public FullName name() {
        return name;
    }

    public EmailAddress email() {
        return email;
    }

    public StudentStatus status() {
        return status;
    }

    public Instant enrolledAt() {
        return enrolledAt;
    }

    public PromotionId promotionId() {
        return promotionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Student other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
