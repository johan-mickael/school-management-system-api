import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { CoursesApi } from '../../../core/api/courses.api';
import { GradesApi } from '../../../core/api/grades.api';
import { PromotionsApi } from '../../../core/api/promotions.api';
import { AuthService } from '../../../core/auth/auth.service';
import { CourseResponse, GradeResponse, PromotionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-teacher-grading',
  imports: [FormsModule, Feedback],
  templateUrl: './teacher-grading.html',
})
export class TeacherGrading {
  private readonly auth = inject(AuthService);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly coursesApi = inject(CoursesApi);
  private readonly gradesApi = inject(GradesApi);

  readonly isAdmin = this.auth.hasRole('ADMIN');
  readonly personId = this.auth.claims()?.personId ?? null;

  readonly promotions = signal<PromotionResponse[]>([]);
  readonly courses = signal<CourseResponse[]>([]);
  readonly selectedCourseId = signal<string | null>(null);

  readonly visibleCourses = computed(() =>
    this.isAdmin ? this.courses() : this.courses().filter((c) => c.teacherId === this.personId),
  );

  readonly error = signal<string | null>(null);
  readonly recording = signal(false);
  readonly recordedMessage = signal<string | null>(null);

  readonly gradeStudentId = signal('');
  readonly gradeExamId = signal('');
  readonly gradeScore = signal(10);
  readonly gradeCoefficient = signal(1);

  readonly lookupStudentId = signal('');
  readonly lookupGrades = signal<GradeResponse[]>([]);
  readonly correctingId = signal<string | null>(null);
  readonly correctingScore = signal(10);

  constructor() {
    this.promotionsApi.listActive().subscribe({
      next: (promotions) => this.promotions.set(promotions),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  selectPromotion(promotionId: string): void {
    this.selectedCourseId.set(null);
    if (!promotionId) {
      this.courses.set([]);
      return;
    }
    this.coursesApi.listByPromotion(promotionId).subscribe({
      next: (courses) => this.courses.set(courses),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  recordGrade(): void {
    const courseId = this.selectedCourseId();
    if (!courseId) {
      return;
    }
    this.recording.set(true);
    this.recordedMessage.set(null);
    this.gradesApi
      .record(courseId, {
        studentId: this.gradeStudentId(),
        examId: this.gradeExamId() || null,
        score: this.gradeScore(),
        coefficient: this.gradeCoefficient(),
      })
      .subscribe({
        next: () => {
          this.recording.set(false);
          this.recordedMessage.set('Grade recorded.');
          this.gradeStudentId.set('');
          this.gradeExamId.set('');
        },
        error: (err) => {
          this.recording.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  lookupGradesForStudent(): void {
    const studentId = this.lookupStudentId();
    if (!studentId) {
      return;
    }
    this.gradesApi.getStudentGrades(studentId).subscribe({
      next: (grades) => this.lookupGrades.set(grades),
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
        this.lookupGrades.update((list) => list.map((g) => (g.id === updated.id ? updated : g)));
        this.correctingId.set(null);
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
