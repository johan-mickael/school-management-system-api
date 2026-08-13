CREATE TABLE
    teachers (
        id UUID PRIMARY KEY,
        number VARCHAR(20) NOT NULL UNIQUE,
        first_name VARCHAR(100) NOT NULL,
        last_name VARCHAR(100) NOT NULL,
        email VARCHAR(255) NOT NULL UNIQUE,
        status VARCHAR(20) NOT NULL,
        hired_at TIMESTAMPTZ NOT NULL
    );
