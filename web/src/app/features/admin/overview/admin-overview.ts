import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { PromotionsApi } from '../../../core/api/promotions.api';
import { TeachersApi } from '../../../core/api/teachers.api';
import { NavIcon, NavIconComponent } from '../../../shared/ui/nav-icon';
import { errorMessage } from '../../../shared/api-error';
import { Feedback } from '../../../shared/ui/feedback';

interface QuickLink {
  path: string;
  label: string;
  description: string;
  icon: NavIcon;
}

const QUICK_LINKS: QuickLink[] = [
  { path: '/admin/promotions', label: 'Promotions', description: 'Create promotions, enroll students, courses and sessions.', icon: 'promotions' },
  { path: '/admin/teachers', label: 'Teachers', description: 'Hire and archive teachers.', icon: 'teachers' },
  { path: '/admin/users', label: 'Users', description: 'Register login accounts.', icon: 'users' },
  { path: '/admin/reporting', label: 'Reporting', description: 'Promotion summary and at-risk students.', icon: 'reporting' },
  { path: '/admin/archive', label: 'Archive', description: 'Browse archived promotions and their rosters.', icon: 'archive' },
];

/** Home of the admin backoffice — headline stats plus the section quick links. */
@Component({
  selector: 'app-admin-overview',
  imports: [RouterLink, NavIconComponent, Feedback],
  templateUrl: './admin-overview.html',
})
export class AdminOverview {
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly teachersApi = inject(TeachersApi);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly activePromotions = signal<number | null>(null);
  readonly totalEnrolled = signal<number | null>(null);
  readonly activeTeachers = signal<number | null>(null);

  readonly quickLinks = QUICK_LINKS;

  constructor() {
    forkJoin({
      promotions: this.promotionsApi.listActive(),
      teachers: this.teachersApi.list(),
    }).subscribe({
      next: ({ promotions, teachers }) => {
        this.activePromotions.set(promotions.length);
        this.totalEnrolled.set(promotions.reduce((sum, p) => sum + p.occupancy, 0));
        this.activeTeachers.set(teachers.filter((t) => t.status === 'ACTIVE').length);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
