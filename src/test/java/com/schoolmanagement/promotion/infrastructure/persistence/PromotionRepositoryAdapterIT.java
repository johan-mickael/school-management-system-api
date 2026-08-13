package com.schoolmanagement.promotion.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionFull;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.shared.AbstractIntegrationTest;

class PromotionRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private PromotionRepository promotions;

  @Test
  @Transactional
  void saves_and_retrieves_a_promotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));

    promotions.save(promotion);

    Promotion found = promotions.getById(promotion.id());

    assertThat(found.id()).isEqualTo(promotion.id());
    assertThat(found.name()).isEqualTo(promotion.name());
    assertThat(found.academicYear()).isEqualTo(promotion.academicYear());
    assertThat(found.capacity()).isEqualTo(promotion.capacity());
    assertThat(found.occupancy()).isZero();
  }

  @Test
  @Transactional
  void throws_when_promotion_not_found() {
    assertThatThrownBy(() -> promotions.getById(PromotionId.generate()))
        .isInstanceOf(PromotionNotFound.class);
  }

  @Test
  @Transactional
  void rejects_admission_once_capacity_is_reached() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(1));
    promotions.save(promotion);

    Promotion full = promotions.getById(promotion.id());
    full.admit();
    promotions.save(full);

    Promotion reloaded = promotions.getById(promotion.id());
    assertThatThrownBy(reloaded::admit).isInstanceOf(PromotionFull.class);
  }

  @Test
  void detects_concurrent_admissions_via_optimistic_locking() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);

    Promotion staleCopy = promotions.getById(promotion.id());
    Promotion freshCopy = promotions.getById(promotion.id());

    freshCopy.admit();
    promotions.save(freshCopy);

    staleCopy.admit();
    assertThatThrownBy(() -> promotions.save(staleCopy))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);
  }
}
