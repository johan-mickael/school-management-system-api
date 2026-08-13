package com.schoolmanagement.scheduling.application.query.listsessions;

import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.SessionRepository;

@Service
public class ListSessionsHandler {
  private final SessionRepository sessions;

  public ListSessionsHandler(SessionRepository sessions) {
    this.sessions = sessions;
  }

  @Transactional(readOnly = true)
  public List<SessionView> handle(ListSessionsQuery query) {
    var found = sessions.findByPromotionId(PromotionId.of(query.promotionId()));

    if (query.date() != null) {
      found = found.stream()
          .filter(s -> s.timeWindow().start().atZone(ZoneOffset.UTC).toLocalDate().equals(query.date()))
          .toList();
    }

    return found.stream().map(SessionView::from).toList();
  }
}
