CREATE TABLE
    promotions (
        id UUID PRIMARY KEY,
        name VARCHAR(150) NOT NULL,
        academic_year VARCHAR(9) NOT NULL,
        capacity INTEGER NOT NULL,
        occupancy INTEGER NOT NULL DEFAULT 0
    );
