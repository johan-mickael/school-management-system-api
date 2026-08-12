package com.schoolmanagement.student.domain;

import java.time.Instant;
import java.util.Objects;

import com.schoolmanagement.student.domain.exception.StudentAlreadyArchived;

public final class Student {
    private final StudentId id;
    private final StudentNumber number;
    private final FullName name;
    private final EmailAddress email;
    private StudentStatus status;
    private final Instant enrolledAt;

    private Student(StudentId id, StudentNumber number, FullName name,
            EmailAddress email, StudentStatus status, Instant enrolledAt) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.email = email;
        this.status = status;
        this.enrolledAt = enrolledAt;
    }

    public static Student enroll(StudentId id, StudentNumber number,
            FullName name, EmailAddress email, Instant enrolledAt) {
        return new Student(id, number, name, email, StudentStatus.ENROLLED, enrolledAt);
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
