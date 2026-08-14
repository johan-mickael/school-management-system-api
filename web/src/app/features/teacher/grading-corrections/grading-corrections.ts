import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { GradesApi } from '../../../core/api/grades.api';
import { GradeResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-grading-corrections',
  imports: [FormsModule, RouterLink, EmptyState, Feedback],
  templateUrl: './grading-corrections.html',
})
export class GradingCorrections {
  private readonly gradesApi = inject(GradesApi);

  readonly error = signal<string | null>(null);
  readonly searched = signal(false);

  readonly studentId = signal('');
  readonly grades = signal<GradeResponse[]>([]);
  readonly correctingId = signal<string | null>(null);
  readonly correctingScore = signal(10);

  search(): void {
    const studentId = this.studentId();
    if (!studentId) {
      return;
    }
    this.gradesApi.getStudentGrades(studentId).subscribe({
      next: (grades) => {
        this.grades.set(grades);
        this.searched.set(true);
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  startCorrect(grade: GradeResponse): void {
    this.correctingId.set(grade.id);
    this.correctingScore.set(grade.score);
  }

  submitCorrect(gradeId: string): void {
    this.gradesApi.correct(gradeId, this.correctingScore()).subscribe({
      next: (updated) => {
        this.grades.update((list) => list.map((g) => (g.id === updated.id ? updated : g)));
        this.correctingId.set(null);
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
