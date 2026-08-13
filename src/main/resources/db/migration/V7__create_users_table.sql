CREATE TABLE
    users (
        id UUID PRIMARY KEY,
        username VARCHAR(100) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        role VARCHAR(20) NOT NULL,
        person_id UUID,
        enabled BOOLEAN NOT NULL DEFAULT TRUE
    );
