CREATE TABLE
    exam_attempts (
        id UUID PRIMARY KEY,
        exam_id UUID NOT NULL REFERENCES exams (id),
        student_id UUID NOT NULL REFERENCES students (id),
        status VARCHAR(20) NOT NULL,
        version BIGINT NOT NULL DEFAULT 0,
        UNIQUE (exam_id, student_id)
    );

CREATE TABLE
    integrity_events (
        attempt_id UUID NOT NULL REFERENCES exam_attempts (id),
        event_order INTEGER NOT NULL,
        type VARCHAR(30) NOT NULL,
        occurred_at TIMESTAMPTZ NOT NULL,
        PRIMARY KEY (attempt_id, event_order)
    );
