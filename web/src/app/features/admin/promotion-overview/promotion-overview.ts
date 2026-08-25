import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { CoursesApi } from '../../../core/api/courses.api';
import { PromotionsApi } from '../../../core/api/promotions.api';
import { SessionsApi } from '../../../core/api/sessions.api';
import { errorMessage } from '../../../shared/api-error';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-promotion-overview',
  imports: [RouterLink, Feedback],
  templateUrl: './promotion-overview.html',
})
export class PromotionOverview {
  private readonly route = inject(ActivatedRoute);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly coursesApi = inject(CoursesApi);
  private readonly sessionsApi = inject(SessionsApi);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly occupancy = signal(0);
  readonly capacity = signal(0);
  readonly courseCount = signal(0);
  readonly sessionCount = signal(0);

  constructor() {
    forkJoin({
      promotion: this.promotionsApi.getOne(this.promotionId),
      courses: this.coursesApi.listByPromotion(this.promotionId),
      sessions: this.sessionsApi.listByPromotion(this.promotionId),
    }).subscribe({
      next: ({ promotion, courses, sessions }) => {
        this.occupancy.set(promotion.occupancy);
        this.capacity.set(promotion.capacity);
        this.courseCount.set(courses.length);
        this.sessionCount.set(sessions.length);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
