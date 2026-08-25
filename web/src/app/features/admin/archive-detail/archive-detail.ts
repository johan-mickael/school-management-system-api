import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { ArchiveApi } from '../../../core/api/archive.api';
import { BreadcrumbService } from '../../../core/breadcrumb.service';
import { PromotionsApi } from '../../../core/api/promotions.api';
import { PromotionResponse, StudentResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Avatar } from '../../../shared/ui/avatar';
import { Chip } from '../../../shared/ui/chip';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-archive-detail',
  imports: [RouterLink, Avatar, Chip, EmptyState, Feedback],
  templateUrl: './archive-detail.html',
})
export class ArchiveDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly archiveApi = inject(ArchiveApi);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly breadcrumb = inject(BreadcrumbService);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly promotion = signal<PromotionResponse | null>(null);
  readonly students = signal<StudentResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  constructor() {
    inject(DestroyRef).onDestroy(() => this.breadcrumb.clear());
    forkJoin({
      promotion: this.archiveApi.getPromotion(this.promotionId),
      students: this.promotionsApi.getStudents(this.promotionId),
    }).subscribe({
      next: ({ promotion, students }) => {
        this.promotion.set(promotion);
        this.breadcrumb.set(promotion.name);
        this.students.set(students);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
