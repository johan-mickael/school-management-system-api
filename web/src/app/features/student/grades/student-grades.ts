import { Component, inject, signal } from '@angular/core';
import { forkJoin } from 'rxjs';

import { GradesApi } from '../../../core/api/grades.api';
import { AuthService } from '../../../core/auth/auth.service';
import { GradeResponse, StudentAveragesResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';

@Component({
  selector: 'app-student-grades',
  templateUrl: './student-grades.html',
})
export class StudentGrades {
  private readonly auth = inject(AuthService);
  private readonly gradesApi = inject(GradesApi);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly grades = signal<GradeResponse[]>([]);
  readonly averages = signal<StudentAveragesResponse | null>(null);

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
    }).subscribe({
      next: ({ grades, averages }) => {
        this.grades.set(grades);
        this.averages.set(averages);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
