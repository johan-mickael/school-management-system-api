ALTER TABLE students
    ADD COLUMN promotion_id UUID REFERENCES promotions (id);
