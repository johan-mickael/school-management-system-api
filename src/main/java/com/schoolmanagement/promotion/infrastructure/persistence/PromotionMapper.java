package com.schoolmanagement.promotion.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;

@Component
public class PromotionMapper {

  public PromotionEntity toEntity(Promotion p) {
    return new PromotionEntity(
        p.id().value(),
        p.name().value(),
        p.academicYear().value(),
        p.capacity().value(),
        p.occupancy());
  }

  public Promotion toDomain(PromotionEntity e) {
    return Promotion.reconstitute(
        new PromotionId(e.getId()),
        new PromotionName(e.getName()),
        new AcademicYear(e.getAcademicYear()),
        new Capacity(e.getCapacity()),
        e.getOccupancy());
  }
}
