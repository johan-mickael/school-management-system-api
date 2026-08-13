package com.schoolmanagement.promotion.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.PromotionStatus;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;

@Repository
public class PromotionRepositoryAdapter implements PromotionRepository {
  private final PromotionJpaRepository promotionRepository;
  private final PromotionMapper promotionMapper;

  public PromotionRepositoryAdapter(PromotionJpaRepository jpa, PromotionMapper mapper) {
    this.promotionRepository = jpa;
    this.promotionMapper = mapper;
  }

  @Override
  public void save(Promotion promotion) {
    promotionRepository.save(promotionMapper.toEntity(promotion));
  }

  @Override
  public Promotion getById(PromotionId id) {
    return promotionRepository.findById(id.value())
        .map(promotionMapper::toDomain)
        .orElseThrow(() -> new PromotionNotFound(id));
  }

  @Override
  public List<Promotion> findArchived() {
    return promotionRepository.findByStatus(PromotionStatus.ARCHIVED).stream()
        .map(promotionMapper::toDomain)
        .toList();
  }
}
