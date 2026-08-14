import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { ArchiveApi } from '../../../core/api/archive.api';
import { PromotionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-archive-promotions',
  imports: [RouterLink, Chip, EmptyState, Feedback],
  templateUrl: './archive-promotions.html',
})
export class ArchivePromotions {
  private readonly archiveApi = inject(ArchiveApi);

  readonly promotions = signal<PromotionResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  constructor() {
    this.archiveApi.listPromotions().subscribe({
      next: (promotions) => {
        this.promotions.set(promotions);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
