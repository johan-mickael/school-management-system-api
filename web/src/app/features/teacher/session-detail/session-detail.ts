import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { AttendanceApi } from '../../../core/api/attendance.api';
import { PromotionsApi } from '../../../core/api/promotions.api';
import { SessionsApi } from '../../../core/api/sessions.api';
import { AttendanceRecordResponse, SessionResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-session-detail',
  imports: [DatePipe, RouterLink, FormsModule, Chip, Feedback],
  templateUrl: './session-detail.html',
})
export class SessionDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly sessionsApi = inject(SessionsApi);
  private readonly attendanceApi = inject(AttendanceApi);
  private readonly promotionsApi = inject(PromotionsApi);

  readonly sessionId = this.route.snapshot.paramMap.get('id')!;
  readonly session = signal<SessionResponse | null>(null);
  readonly records = signal<AttendanceRecordResponse[]>([]);
  readonly studentNames = signal<Map<string, string>>(new Map());
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly justifyingId = signal<string | null>(null);
  readonly justification = signal('');

  constructor() {
    this.load();
  }

  studentLabel(studentId: string): string {
    return this.studentNames().get(studentId) ?? studentId;
  }

  private load(): void {
    this.sessionsApi.getOne(this.sessionId).subscribe({
      next: (session) => {
        this.session.set(session);
        this.promotionsApi.getStudents(session.promotionId).subscribe({
          next: (students) => {
            this.studentNames.set(new Map(students.map((s) => [s.id, `${s.firstName} ${s.lastName}`])));
          },
        });
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
    this.attendanceApi.getSessionAttendance(this.sessionId).subscribe({
      next: (records) => {
        this.records.set(records);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  startJustify(studentId: string): void {
    this.justifyingId.set(studentId);
    this.justification.set('');
  }

  submitJustify(studentId: string): void {
    this.attendanceApi.justify(this.sessionId, studentId, this.justification()).subscribe({
      next: () => {
        this.justifyingId.set(null);
        this.load();
      },
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
