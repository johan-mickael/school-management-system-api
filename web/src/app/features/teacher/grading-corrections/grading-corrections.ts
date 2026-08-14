import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { CoursesApi } from '../../../core/api/courses.api';
import { GradesApi } from '../../../core/api/grades.api';
import { StudentsApi } from '../../../core/api/students.api';
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
  private readonly studentsApi = inject(StudentsApi);
  private readonly coursesApi = inject(CoursesApi);
  private readonly gradesApi = inject(GradesApi);

  readonly error = signal<string | null>(null);
  readonly searched = signal(false);

  readonly studentId = signal('');
  readonly grades = signal<GradeResponse[]>([]);
  readonly courseNames = signal<Map<string, string>>(new Map());
  readonly correctingId = signal<string | null>(null);
  readonly correctingScore = signal(10);

  courseLabel(courseId: string): string {
    return this.courseNames().get(courseId) ?? courseId;
  }

  search(): void {
    const studentId = this.studentId();
    if (!studentId) {
      return;
    }
    forkJoin({
      grades: this.gradesApi.getStudentGrades(studentId),
      student: this.studentsApi.getOne(studentId),
    }).subscribe({
      next: ({ grades, student }) => {
        this.grades.set(grades);
        this.searched.set(true);
        if (student.promotionId) {
          this.coursesApi.listByPromotion(student.promotionId).subscribe({
            next: (courses) => {
              this.courseNames.set(new Map(courses.map((c) => [c.id, `${c.code} — ${c.title}`])));
            },
          });
        }
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
