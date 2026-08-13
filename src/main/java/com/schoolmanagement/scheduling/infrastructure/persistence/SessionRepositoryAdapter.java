package com.schoolmanagement.scheduling.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.scheduling.domain.exception.SessionNotFound;

@Repository
public class SessionRepositoryAdapter implements SessionRepository {
  private final SessionJpaRepository sessionRepository;
  private final SessionMapper sessionMapper;

  public SessionRepositoryAdapter(SessionJpaRepository jpa, SessionMapper mapper) {
    this.sessionRepository = jpa;
    this.sessionMapper = mapper;
  }

  @Override
  public void save(Session session) {
    sessionRepository.save(sessionMapper.toEntity(session));
  }

  @Override
  public Session getById(SessionId id) {
    return sessionRepository.findById(id.value())
        .map(sessionMapper::toDomain)
        .orElseThrow(() -> new SessionNotFound(id));
  }

  @Override
  public List<Session> findByPromotionId(PromotionId promotionId) {
    return sessionRepository.findByPromotionId(promotionId.value()).stream()
        .map(sessionMapper::toDomain)
        .toList();
  }
}
