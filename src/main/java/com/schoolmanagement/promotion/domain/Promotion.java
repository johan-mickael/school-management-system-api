package com.schoolmanagement.promotion.domain;

import java.util.Objects;

import com.schoolmanagement.promotion.domain.exception.PromotionFull;
import com.schoolmanagement.promotion.domain.exception.PromotionNotOccupied;

public final class Promotion {
    private final PromotionId id;
    private final PromotionName name;
    private final AcademicYear academicYear;
    private final Capacity capacity;
    private int occupancy;
    private final long version;

    private Promotion(PromotionId id,
            PromotionName name,
            AcademicYear academicYear,
            Capacity capacity,
            int occupancy,
            long version) {
        this.id = id;
        this.name = name;
        this.academicYear = academicYear;
        this.capacity = capacity;
        this.occupancy = occupancy;
        this.version = version;
    }

    public static Promotion create(PromotionId id,
            PromotionName name,
            AcademicYear academicYear,
            Capacity capacity) {
        return new Promotion(id, name, academicYear, capacity, 0, 0L);
    }

    public static Promotion reconstitute(PromotionId id,
            PromotionName name,
            AcademicYear academicYear,
            Capacity capacity,
            int occupancy,
            long version) {
        return new Promotion(id, name, academicYear, capacity, occupancy, version);
    }

    public void admit() {
        if (occupancy >= capacity.value()) {
            throw new PromotionFull(id);
        }
        occupancy++;
    }

    public void release() {
        if (occupancy <= 0) {
            throw new PromotionNotOccupied(id);
        }
        occupancy--;
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

    public long version() {
        return version;
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
