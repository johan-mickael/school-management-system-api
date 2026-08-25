import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { GradesApi } from '../../core/api/grades.api';
import { AuthService } from '../../core/auth/auth.service';
import { NavIcon, NavIconComponent } from '../../shared/ui/nav-icon';
import { errorMessage } from '../../shared/api-error';
import { Feedback } from '../../shared/ui/feedback';

interface QuickLink {
  path: string;
  label: string;
  description: string;
  icon: NavIcon;
}

/** Home for the plain UserShell — STUDENT and TEACHER only. Admin has its own AdminOverview. */
@Component({
  selector: 'app-dashboard',
  imports: [RouterLink, NavIconComponent, Feedback],
  templateUrl: './dashboard.html',
})
export class Dashboard {
  private readonly auth = inject(AuthService);
  private readonly gradesApi = inject(GradesApi);

  readonly role = this.auth.claims()?.role ?? null;
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly overallAverage = signal<number | null>(null);

  readonly quickLinks: QuickLink[] = this.buildQuickLinks();

  constructor() {
    if (this.role === 'STUDENT') {
      this.loadStudent();
    } else {
      this.loading.set(false);
    }
  }

  private loadStudent(): void {
    const personId = this.auth.claims()?.personId;
    if (!personId) {
      this.loading.set(false);
      return;
    }
    this.gradesApi.getStudentAverages(personId).subscribe({
      next: (averages) => {
        this.overallAverage.set(averages.overallAverage);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  private buildQuickLinks(): QuickLink[] {
    if (this.role === 'STUDENT') {
      return [
        { path: '/attendance', label: 'Attendance', description: 'Sign in to open sessions, view history.', icon: 'attendance' },
        { path: '/grades', label: 'Grades', description: 'Your scores and course averages.', icon: 'grades' },
      ];
    }
    if (this.role === 'TEACHER') {
      return [
        { path: '/teacher/sessions', label: 'Sessions', description: 'Open, close and cancel signing windows.', icon: 'sessions' },
        { path: '/teacher/grading', label: 'Record grades', description: 'Enter a score for a student.', icon: 'grading' },
        { path: '/teacher/grading/corrections', label: 'Correct grades', description: 'Look up and amend a recorded grade.', icon: 'correct' },
        { path: '/reporting', label: 'Reporting', description: 'Promotion summary and at-risk students.', icon: 'reporting' },
      ];
    }
    return [];
  }
}
