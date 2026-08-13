CREATE TABLE
    grades (
        id UUID PRIMARY KEY,
        student_id UUID NOT NULL REFERENCES students (id),
        course_id UUID NOT NULL REFERENCES courses (id),
        exam_id UUID,
        score DOUBLE PRECISION NOT NULL,
        coefficient DOUBLE PRECISION NOT NULL
    );
