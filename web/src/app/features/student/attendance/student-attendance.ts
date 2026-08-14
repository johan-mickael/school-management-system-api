import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';

import { AttendanceApi } from '../../../core/api/attendance.api';
import { SessionsApi } from '../../../core/api/sessions.api';
import { StudentsApi } from '../../../core/api/students.api';
import { AuthService } from '../../../core/auth/auth.service';
import { AttendanceRecordResponse, SessionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';

@Component({
  selector: 'app-student-attendance',
  imports: [DatePipe],
  templateUrl: './student-attendance.html',
})
export class StudentAttendance {
  private readonly auth = inject(AuthService);
  private readonly studentsApi = inject(StudentsApi);
  private readonly sessionsApi = inject(SessionsApi);
  private readonly attendanceApi = inject(AttendanceApi);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly signingId = signal<string | null>(null);
  readonly todaysSessions = signal<SessionResponse[]>([]);
  readonly history = signal<AttendanceRecordResponse[]>([]);

  private studentId: string | null = null;

  constructor() {
    this.load();
  }

  private load(): void {
    const personId = this.auth.claims()?.personId;
    if (!personId) {
      this.error.set('No student profile is linked to this account.');
      this.loading.set(false);
      return;
    }
    this.studentId = personId;

    this.studentsApi.getOne(personId).subscribe({
      next: (student) => {
        if (!student.promotionId) {
          this.loading.set(false);
          return;
        }
        const today = new Date().toISOString().slice(0, 10);
        forkJoin({
          sessions: this.sessionsApi.listByPromotion(student.promotionId, today),
          history: this.attendanceApi.getStudentAttendance(personId),
        }).subscribe({
          next: ({ sessions, history }) => {
            this.todaysSessions.set(sessions);
            this.history.set(history);
            this.loading.set(false);
          },
          error: (err) => {
            this.error.set(errorMessage(err));
            this.loading.set(false);
          },
        });
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  sign(sessionId: string): void {
    this.signingId.set(sessionId);
    this.attendanceApi.sign(sessionId).subscribe({
      next: () => {
        this.signingId.set(null);
        this.load();
      },
      error: (err) => {
        this.signingId.set(null);
        this.error.set(errorMessage(err));
      },
    });
  }

  alreadySigned(sessionId: string): boolean {
    return this.history().some((record) => record.sessionId === sessionId);
  }
}
