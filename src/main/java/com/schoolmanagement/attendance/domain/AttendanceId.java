package com.schoolmanagement.attendance.domain;

import java.util.UUID;

import com.schoolmanagement.attendance.domain.exception.InvalidAttendanceId;

public record AttendanceId(UUID value) {
    public AttendanceId {
        if (null == value) {
            throw new InvalidAttendanceId("null");
        }
    }

    public static AttendanceId generate() {
        return new AttendanceId(UUID.randomUUID());
    }

    public static AttendanceId of(String raw) {
        try {
            return new AttendanceId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidAttendanceId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
