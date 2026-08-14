import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { PromotionsApi } from '../../../core/api/promotions.api';
import { SessionsApi } from '../../../core/api/sessions.api';
import { AuthService } from '../../../core/auth/auth.service';
import { PromotionResponse, SessionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-teacher-sessions',
  imports: [DatePipe, RouterLink, Chip, EmptyState, Feedback],
  templateUrl: './teacher-sessions.html',
})
export class TeacherSessions {
  private readonly auth = inject(AuthService);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly sessionsApi = inject(SessionsApi);

  readonly isAdmin = this.auth.hasRole('ADMIN');
  readonly personId = this.auth.claims()?.personId ?? null;

  readonly promotions = signal<PromotionResponse[]>([]);
  readonly selectedPromotionId = signal<string | null>(null);
  readonly sessions = signal<SessionResponse[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly actingOn = signal<string | null>(null);

  readonly visibleSessions = computed(() => {
    if (this.isAdmin) {
      return this.sessions();
    }
    return this.sessions().filter((session) => session.teacherId === this.personId);
  });

  constructor() {
    this.promotionsApi.listActive().subscribe({
      next: (promotions) => this.promotions.set(promotions),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  selectPromotion(promotionId: string): void {
    this.selectedPromotionId.set(promotionId || null);
    if (!promotionId) {
      this.sessions.set([]);
      return;
    }
    this.loading.set(true);
    this.sessionsApi.listByPromotion(promotionId).subscribe({
      next: (sessions) => {
        this.sessions.set(sessions);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  act(session: SessionResponse, action: 'open' | 'close' | 'cancel'): void {
    this.actingOn.set(session.id);
    const call =
      action === 'open'
        ? this.sessionsApi.open(session.id)
        : action === 'close'
          ? this.sessionsApi.close(session.id)
          : this.sessionsApi.cancel(session.id);

    call.subscribe({
      next: (updated) => {
        this.sessions.update((list) => list.map((s) => (s.id === updated.id ? updated : s)));
        this.actingOn.set(null);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.actingOn.set(null);
      },
    });
  }
}
