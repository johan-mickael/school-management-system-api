import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { GradesApi } from '../../core/api/grades.api';
import { PromotionsApi } from '../../core/api/promotions.api';
import { ReportingApi } from '../../core/api/reporting.api';
import {
  AtRiskStudentResponse,
  PromotionRankingEntryResponse,
  PromotionResponse,
  PromotionSummaryResponse,
} from '../../core/api-models';
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
  private readonly gradesApi = inject(GradesApi);

  readonly promotions = signal<PromotionResponse[]>([]);
  readonly summary = signal<PromotionSummaryResponse | null>(null);
  readonly atRisk = signal<AtRiskStudentResponse[]>([]);
  readonly rankings = signal<PromotionRankingEntryResponse[]>([]);
  readonly studentNames = signal<Map<string, string>>(new Map());
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  constructor() {
    this.promotionsApi.listActive().subscribe({
      next: (promotions) => this.promotions.set(promotions),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  studentLabel(studentId: string): string {
    return this.studentNames().get(studentId) ?? studentId;
  }

  selectPromotion(promotionId: string): void {
    if (!promotionId) {
      this.summary.set(null);
      this.atRisk.set([]);
      this.rankings.set([]);
      this.studentNames.set(new Map());
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
    this.gradesApi.getPromotionRankings(promotionId).subscribe({
      next: (rankings) => this.rankings.set(rankings),
      error: (err) => this.error.set(errorMessage(err)),
    });
    this.promotionsApi.getStudents(promotionId).subscribe({
      next: (students) => {
        this.studentNames.set(
          new Map(students.map((s) => [s.id, `${s.firstName} ${s.lastName}`])),
        );
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
