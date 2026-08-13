CREATE TABLE
    sessions (
        id UUID PRIMARY KEY,
        course_id UUID NOT NULL REFERENCES courses (id),
        promotion_id UUID NOT NULL REFERENCES promotions (id),
        teacher_id UUID NOT NULL REFERENCES teachers (id),
        starts_at TIMESTAMPTZ NOT NULL,
        ends_at TIMESTAMPTZ NOT NULL,
        grace_period_seconds BIGINT NOT NULL,
        status VARCHAR(20) NOT NULL,
        version BIGINT NOT NULL DEFAULT 0
    );
