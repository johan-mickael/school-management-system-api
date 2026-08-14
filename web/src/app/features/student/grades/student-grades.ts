import { Component, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';

import { CoursesApi } from '../../../core/api/courses.api';
import { GradesApi } from '../../../core/api/grades.api';
import { StudentsApi } from '../../../core/api/students.api';
import { AuthService } from '../../../core/auth/auth.service';
import { GradeResponse, StudentAveragesResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-student-grades',
  imports: [EmptyState, Feedback],
  templateUrl: './student-grades.html',
})
export class StudentGrades {
  private readonly auth = inject(AuthService);
  private readonly studentsApi = inject(StudentsApi);
  private readonly coursesApi = inject(CoursesApi);
  private readonly gradesApi = inject(GradesApi);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly grades = signal<GradeResponse[]>([]);
  readonly averages = signal<StudentAveragesResponse | null>(null);
  readonly courseNames = signal<Map<string, string>>(new Map());

  constructor() {
    const personId = this.auth.claims()?.personId;
    if (!personId) {
      this.error.set('No student profile is linked to this account.');
      this.loading.set(false);
      return;
    }

    forkJoin({
      grades: this.gradesApi.getStudentGrades(personId),
      averages: this.gradesApi.getStudentAverages(personId),
      student: this.studentsApi.getOne(personId),
    }).subscribe({
      next: ({ grades, averages, student }) => {
        this.grades.set(grades);
        this.averages.set(averages);
        this.loading.set(false);

        if (student.promotionId) {
          this.coursesApi.listByPromotion(student.promotionId).subscribe({
            next: (courses) => {
              this.courseNames.set(new Map(courses.map((c) => [c.id, `${c.code} — ${c.title}`])));
            },
          });
        }
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  courseLabel(courseId: string): string {
    return this.courseNames().get(courseId) ?? courseId;
  }
}
