import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { PromotionsApi } from '../../../core/api/promotions.api';
import { PromotionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-promotion-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Chip, Feedback],
  templateUrl: './promotion-shell.html',
})
export class PromotionShell {
  private readonly route = inject(ActivatedRoute);
  private readonly promotionsApi = inject(PromotionsApi);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly promotion = signal<PromotionResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly archiving = signal(false);

  constructor() {
    this.load();
  }

  private load(): void {
    this.promotionsApi.getOne(this.promotionId).subscribe({
      next: (promotion) => {
        this.promotion.set(promotion);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  archive(): void {
    this.archiving.set(true);
    this.promotionsApi.archive(this.promotionId).subscribe({
      next: () => {
        this.archiving.set(false);
        this.load();
      },
      error: (err) => {
        this.archiving.set(false);
        this.error.set(errorMessage(err));
      },
    });
  }
}
