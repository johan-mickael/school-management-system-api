package com.schoolmanagement.teacher.domain;

import java.time.Instant;
import java.util.Objects;

import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.teacher.domain.exception.TeacherAlreadyArchived;

public final class Teacher {
    private final TeacherId id;
    private final StaffNumber number;
    private final FullName name;
    private final EmailAddress email;
    private TeacherStatus status;
    private final Instant hiredAt;

    private Teacher(TeacherId id,
            StaffNumber number,
            FullName name,
            EmailAddress email,
            TeacherStatus status,
            Instant hiredAt) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.email = email;
        this.status = status;
        this.hiredAt = hiredAt;
    }

    public static Teacher hire(TeacherId id,
            StaffNumber number,
            FullName name,
            EmailAddress email,
            Instant hiredAt) {
        return new Teacher(id, number, name, email, TeacherStatus.ACTIVE, hiredAt);
    }

    public void archive() {
        if (status == TeacherStatus.ARCHIVED) {
            throw new TeacherAlreadyArchived(id);
        }
        this.status = TeacherStatus.ARCHIVED;
    }

    public boolean isArchived() {
        return status == TeacherStatus.ARCHIVED;
    }

    public static Teacher reconstitute(TeacherId id,
            StaffNumber number,
            FullName name,
            EmailAddress email,
            TeacherStatus status,
            Instant hiredAt) {
        return new Teacher(id, number, name, email, status, hiredAt);
    }

    public TeacherId id() {
        return id;
    }

    public StaffNumber number() {
        return number;
    }

    public FullName name() {
        return name;
    }

    public EmailAddress email() {
        return email;
    }

    public TeacherStatus status() {
        return status;
    }

    public Instant hiredAt() {
        return hiredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Teacher other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
