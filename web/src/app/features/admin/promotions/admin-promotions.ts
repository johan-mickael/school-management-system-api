import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { PromotionsApi } from '../../../core/api/promotions.api';
import { PromotionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-admin-promotions',
  imports: [FormsModule, RouterLink, Chip, EmptyState, Feedback],
  templateUrl: './admin-promotions.html',
})
export class AdminPromotions {
  private readonly api = inject(PromotionsApi);

  readonly promotions = signal<PromotionResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly creating = signal(false);

  readonly name = signal('');
  readonly academicYear = signal('');
  readonly capacity = signal(30);

  constructor() {
    this.load();
  }

  private load(): void {
    this.api.listActive().subscribe({
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

  create(): void {
    this.creating.set(true);
    this.api
      .create({ name: this.name(), academicYear: this.academicYear(), capacity: this.capacity() })
      .subscribe({
        next: () => {
          this.creating.set(false);
          this.name.set('');
          this.academicYear.set('');
          this.load();
        },
        error: (err) => {
          this.creating.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }
}
