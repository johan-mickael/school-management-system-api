import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { BreadcrumbService } from '../../../core/breadcrumb.service';
import { PromotionsApi } from '../../../core/api/promotions.api';
import { PromotionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { ConfirmDialogService } from '../../../shared/ui/confirm-dialog.service';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-promotion-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Chip, Feedback],
  templateUrl: './promotion-shell.html',
})
export class PromotionShell {
  private readonly route = inject(ActivatedRoute);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly breadcrumb = inject(BreadcrumbService);
  private readonly confirm = inject(ConfirmDialogService);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly promotion = signal<PromotionResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly archiving = signal(false);

  constructor() {
    this.load();
    inject(DestroyRef).onDestroy(() => this.breadcrumb.clear());
  }

  private load(): void {
    this.promotionsApi.getOne(this.promotionId).subscribe({
      next: (promotion) => {
        this.promotion.set(promotion);
        this.breadcrumb.set(promotion.name);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  async archive(): Promise<void> {
    const ok = await this.confirm.ask({
      title: 'Archive this promotion?',
      message: `${this.promotion()?.name ?? 'This promotion'} will become read-only — no further enrollment, course, or session changes. This can't be undone.`,
      confirmLabel: 'Archive promotion',
      destructive: true,
    });
    if (!ok) {
      return;
    }
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
