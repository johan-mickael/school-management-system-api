package com.schoolmanagement.promotion.domain;

import java.util.Objects;

import com.schoolmanagement.promotion.domain.exception.PromotionFull;

public final class Promotion {
    private final PromotionId id;
    private final PromotionName name;
    private final AcademicYear academicYear;
    private final Capacity capacity;
    private int occupancy;

    private Promotion(PromotionId id,
            PromotionName name,
            AcademicYear academicYear,
            Capacity capacity,
            int occupancy) {
        this.id = id;
        this.name = name;
        this.academicYear = academicYear;
        this.capacity = capacity;
        this.occupancy = occupancy;
    }

    public static Promotion create(PromotionId id,
            PromotionName name,
            AcademicYear academicYear,
            Capacity capacity) {
        return new Promotion(id, name, academicYear, capacity, 0);
    }

    public static Promotion reconstitute(PromotionId id,
            PromotionName name,
            AcademicYear academicYear,
            Capacity capacity,
            int occupancy) {
        return new Promotion(id, name, academicYear, capacity, occupancy);
    }

    public void enrollStudent() {
        if (occupancy >= capacity.value()) {
            throw new PromotionFull(id);
        }
        occupancy++;
    }

    public PromotionId id() {
        return id;
    }

    public PromotionName name() {
        return name;
    }

    public AcademicYear academicYear() {
        return academicYear;
    }

    public Capacity capacity() {
        return capacity;
    }

    public int occupancy() {
        return occupancy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Promotion other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
