package com.schoolmanagement.scheduling.domain;

import java.util.Objects;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.scheduling.domain.exception.InvalidSessionTransition;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.teacher.domain.TeacherId;

public final class Session {
    private final SessionId id;
    private final CourseId courseId;
    private final PromotionId promotionId;
    private final TeacherId teacherId;
    private final TimeWindow timeWindow;
    private final GracePeriod gracePeriod;
    private SessionStatus status;
    private final long version;

    private Session(SessionId id,
            CourseId courseId,
            PromotionId promotionId,
            TeacherId teacherId,
            TimeWindow timeWindow,
            GracePeriod gracePeriod,
            SessionStatus status,
            long version) {
        this.id = id;
        this.courseId = courseId;
        this.promotionId = promotionId;
        this.teacherId = teacherId;
        this.timeWindow = timeWindow;
        this.gracePeriod = gracePeriod;
        this.status = status;
        this.version = version;
    }

    public static Session schedule(SessionId id,
            CourseId courseId,
            PromotionId promotionId,
            TeacherId teacherId,
            TimeWindow timeWindow,
            GracePeriod gracePeriod) {
        return new Session(id, courseId, promotionId, teacherId, timeWindow, gracePeriod, SessionStatus.SCHEDULED, 0L);
    }

    public static Session reconstitute(SessionId id,
            CourseId courseId,
            PromotionId promotionId,
            TeacherId teacherId,
            TimeWindow timeWindow,
            GracePeriod gracePeriod,
            SessionStatus status,
            long version) {
        return new Session(id, courseId, promotionId, teacherId, timeWindow, gracePeriod, status, version);
    }

    public void openSigning() {
        if (status != SessionStatus.SCHEDULED) {
            throw new InvalidSessionTransition(id, status, "open signing for");
        }
        status = SessionStatus.SIGNING_OPEN;
    }

    public void closeSigning() {
        if (status != SessionStatus.SIGNING_OPEN) {
            throw new InvalidSessionTransition(id, status, "close signing for");
        }
        status = SessionStatus.SIGNING_CLOSED;
    }

    public void cancel() {
        if (status != SessionStatus.SCHEDULED && status != SessionStatus.SIGNING_OPEN) {
            throw new InvalidSessionTransition(id, status, "cancel");
        }
        status = SessionStatus.CANCELLED;
    }

    public SessionId id() {
        return id;
    }

    public CourseId courseId() {
        return courseId;
    }

    public PromotionId promotionId() {
        return promotionId;
    }

    public TeacherId teacherId() {
        return teacherId;
    }

    public TimeWindow timeWindow() {
        return timeWindow;
    }

    public GracePeriod gracePeriod() {
        return gracePeriod;
    }

    public SessionStatus status() {
        return status;
    }

    public long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Session other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
