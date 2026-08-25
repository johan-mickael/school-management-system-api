import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ReportingApi } from '../../../core/api/reporting.api';
import { StudentSummaryResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-student-lookup',
  imports: [FormsModule, RouterLink, Feedback],
  templateUrl: './student-lookup.html',
})
export class StudentLookup {
  private readonly reportingApi = inject(ReportingApi);

  readonly error = signal<string | null>(null);
  readonly studentId = signal('');
  readonly summary = signal<StudentSummaryResponse | null>(null);

  search(): void {
    const studentId = this.studentId();
    if (!studentId) {
      return;
    }
    this.reportingApi.getStudentSummary(studentId).subscribe({
      next: (summary) => this.summary.set(summary),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
