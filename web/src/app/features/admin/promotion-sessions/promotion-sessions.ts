import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { CoursesApi } from '../../../core/api/courses.api';
import { SessionsApi } from '../../../core/api/sessions.api';
import { CourseResponse, SessionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-promotion-sessions',
  imports: [DatePipe, FormsModule, Chip, EmptyState, Feedback],
  templateUrl: './promotion-sessions.html',
})
export class PromotionSessions {
  private readonly route = inject(ActivatedRoute);
  private readonly coursesApi = inject(CoursesApi);
  private readonly sessionsApi = inject(SessionsApi);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly sessions = signal<SessionResponse[]>([]);
  readonly courses = signal<CourseResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly scheduling = signal(false);

  readonly courseId = signal<string | null>(null);
  readonly teacherId = signal('');
  readonly start = signal('');
  readonly end = signal('');
  readonly graceSeconds = signal(300);

  constructor() {
    this.load();
  }

  private load(): void {
    forkJoin({
      sessions: this.sessionsApi.listByPromotion(this.promotionId),
      courses: this.coursesApi.listByPromotion(this.promotionId),
    }).subscribe({
      next: ({ sessions, courses }) => {
        this.sessions.set(sessions);
        this.courses.set(courses);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  onCourseChange(courseId: string): void {
    this.courseId.set(courseId || null);
    const course = this.courses().find((c) => c.id === courseId);
    this.teacherId.set(course?.teacherId ?? '');
  }

  schedule(): void {
    const courseId = this.courseId();
    if (!courseId) {
      return;
    }
    this.scheduling.set(true);
    this.sessionsApi
      .schedule({
        courseId,
        promotionId: this.promotionId,
        teacherId: this.teacherId(),
        start: new Date(this.start()).toISOString(),
        end: new Date(this.end()).toISOString(),
        gracePeriodSeconds: this.graceSeconds(),
      })
      .subscribe({
        next: () => {
          this.scheduling.set(false);
          this.load();
        },
        error: (err) => {
          this.scheduling.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  act(session: SessionResponse, action: 'open' | 'close' | 'cancel'): void {
    const call =
      action === 'open'
        ? this.sessionsApi.open(session.id)
        : action === 'close'
          ? this.sessionsApi.close(session.id)
          : this.sessionsApi.cancel(session.id);
    call.subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
