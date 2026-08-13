CREATE TABLE
    courses (
        id UUID PRIMARY KEY,
        code VARCHAR(30) NOT NULL UNIQUE,
        title VARCHAR(200) NOT NULL,
        coefficient DOUBLE PRECISION NOT NULL,
        promotion_id UUID NOT NULL REFERENCES promotions (id),
        teacher_id UUID REFERENCES teachers (id)
    );
