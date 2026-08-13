CREATE TABLE
    attendance_records (
        id UUID PRIMARY KEY,
        session_id UUID NOT NULL REFERENCES sessions (id),
        student_id UUID NOT NULL REFERENCES students (id),
        status VARCHAR(20) NOT NULL,
        signed_at TIMESTAMPTZ,
        justification VARCHAR(500),
        UNIQUE (session_id, student_id)
    );
