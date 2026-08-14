import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PromotionsApi } from '../../core/api/promotions.api';
import { ReportingApi } from '../../core/api/reporting.api';
import { AtRiskStudentResponse, PromotionResponse, PromotionSummaryResponse } from '../../core/api-models';
import { errorMessage } from '../../shared/api-error';
import { Chip } from '../../shared/ui/chip';
import { EmptyState } from '../../shared/ui/empty-state';
import { Feedback } from '../../shared/ui/feedback';

@Component({
  selector: 'app-reporting',
  imports: [FormsModule, RouterLink, Chip, EmptyState, Feedback],
  templateUrl: './reporting.html',
})
export class Reporting {
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly reportingApi = inject(ReportingApi);

  readonly promotions = signal<PromotionResponse[]>([]);
  readonly summary = signal<PromotionSummaryResponse | null>(null);
  readonly atRisk = signal<AtRiskStudentResponse[]>([]);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  constructor() {
    this.promotionsApi.listActive().subscribe({
      next: (promotions) => this.promotions.set(promotions),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  selectPromotion(promotionId: string): void {
    if (!promotionId) {
      this.summary.set(null);
      this.atRisk.set([]);
      return;
    }
    this.loading.set(true);
    this.reportingApi.getPromotionSummary(promotionId).subscribe({
      next: (summary) => {
        this.summary.set(summary);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
    this.reportingApi.getAtRiskStudents(promotionId).subscribe({
      next: (students) => this.atRisk.set(students),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
