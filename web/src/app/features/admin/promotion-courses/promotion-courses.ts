import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';

import { CoursesApi } from '../../../core/api/courses.api';
import { TeachersApi } from '../../../core/api/teachers.api';
import { CourseResponse, TeacherResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-promotion-courses',
  imports: [FormsModule, EmptyState, Feedback],
  templateUrl: './promotion-courses.html',
})
export class PromotionCourses {
  private readonly route = inject(ActivatedRoute);
  private readonly coursesApi = inject(CoursesApi);
  private readonly teachersApi = inject(TeachersApi);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly courses = signal<CourseResponse[]>([]);
  readonly teachers = signal<TeacherResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly creating = signal(false);

  readonly code = signal('');
  readonly title = signal('');
  readonly coefficient = signal(1);
  readonly teacherId = signal<string | null>(null);

  constructor() {
    this.load();
  }

  private load(): void {
    forkJoin({
      courses: this.coursesApi.listByPromotion(this.promotionId),
      teachers: this.teachersApi.list(),
    }).subscribe({
      next: ({ courses, teachers }) => {
        this.courses.set(courses);
        this.teachers.set(teachers);
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
    this.coursesApi
      .create({
        code: this.code(),
        title: this.title(),
        coefficient: this.coefficient(),
        promotionId: this.promotionId,
        teacherId: this.teacherId(),
      })
      .subscribe({
        next: () => {
          this.creating.set(false);
          this.code.set('');
          this.title.set('');
          this.teacherId.set(null);
          this.load();
        },
        error: (err) => {
          this.creating.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  assignTeacher(course: CourseResponse, teacherId: string): void {
    if (!teacherId) {
      return;
    }
    this.coursesApi.assignTeacher(course.id, teacherId).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
