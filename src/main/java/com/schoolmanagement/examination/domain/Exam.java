package com.schoolmanagement.examination.domain;

import java.util.Objects;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.examination.domain.exception.InvalidExamTransition;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.shared.domain.TimeWindow;

public final class Exam {
    private final ExamId id;
    private final CourseId courseId;
    private final PromotionId promotionId;
    private final TimeWindow timeWindow;
    private ExamStatus status;
    private final long version;

    private Exam(ExamId id,
            CourseId courseId,
            PromotionId promotionId,
            TimeWindow timeWindow,
            ExamStatus status,
            long version) {
        this.id = id;
        this.courseId = courseId;
        this.promotionId = promotionId;
        this.timeWindow = timeWindow;
        this.status = status;
        this.version = version;
    }

    public static Exam schedule(ExamId id,
            CourseId courseId,
            PromotionId promotionId,
            TimeWindow timeWindow) {
        return new Exam(id, courseId, promotionId, timeWindow, ExamStatus.SCHEDULED, 0L);
    }

    public static Exam reconstitute(ExamId id,
            CourseId courseId,
            PromotionId promotionId,
            TimeWindow timeWindow,
            ExamStatus status,
            long version) {
        return new Exam(id, courseId, promotionId, timeWindow, status, version);
    }

    public void open() {
        if (status != ExamStatus.SCHEDULED) {
            throw new InvalidExamTransition(id, status, "open");
        }
        status = ExamStatus.OPEN;
    }

    public void close() {
        if (status != ExamStatus.OPEN) {
            throw new InvalidExamTransition(id, status, "close");
        }
        status = ExamStatus.CLOSED;
    }

    public ExamId id() {
        return id;
    }

    public CourseId courseId() {
        return courseId;
    }

    public PromotionId promotionId() {
        return promotionId;
    }

    public TimeWindow timeWindow() {
        return timeWindow;
    }

    public ExamStatus status() {
        return status;
    }

    public long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Exam other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
