package com.schoolmanagement.examination.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.schoolmanagement.examination.domain.exception.InvalidAttemptTransition;
import com.schoolmanagement.student.domain.StudentId;

public final class ExamAttempt {
    private final AttemptId id;
    private final ExamId examId;
    private final StudentId studentId;
    private final List<IntegrityEvent> events;
    private AttemptStatus status;
    private final long version;

    private ExamAttempt(AttemptId id,
            ExamId examId,
            StudentId studentId,
            List<IntegrityEvent> events,
            AttemptStatus status,
            long version) {
        this.id = id;
        this.examId = examId;
        this.studentId = studentId;
        this.events = new ArrayList<>(events);
        this.status = status;
        this.version = version;
    }

    public static ExamAttempt start(AttemptId id, ExamId examId, StudentId studentId) {
        return new ExamAttempt(id, examId, studentId, List.of(), AttemptStatus.IN_PROGRESS, 0L);
    }

    public static ExamAttempt reconstitute(AttemptId id,
            ExamId examId,
            StudentId studentId,
            List<IntegrityEvent> events,
            AttemptStatus status,
            long version) {
        return new ExamAttempt(id, examId, studentId, events, status, version);
    }

    public void recordEvent(IntegrityEvent event) {
        if (status != AttemptStatus.IN_PROGRESS) {
            throw new InvalidAttemptTransition(id, status, "record an integrity event for");
        }
        events.add(event);
    }

    public IntegrityScore integrityScore() {
        return new IntegrityScore(events.size());
    }

    public void flag() {
        if (status != AttemptStatus.IN_PROGRESS) {
            throw new InvalidAttemptTransition(id, status, "flag");
        }
        status = AttemptStatus.FLAGGED;
    }

    public void submit() {
        if (status != AttemptStatus.IN_PROGRESS) {
            throw new InvalidAttemptTransition(id, status, "submit");
        }
        status = AttemptStatus.SUBMITTED;
    }

    public AttemptId id() {
        return id;
    }

    public ExamId examId() {
        return examId;
    }

    public StudentId studentId() {
        return studentId;
    }

    public List<IntegrityEvent> events() {
        return List.copyOf(events);
    }

    public AttemptStatus status() {
        return status;
    }

    public long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ExamAttempt other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
