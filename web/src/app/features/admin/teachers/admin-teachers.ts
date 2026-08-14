import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TeachersApi } from '../../../core/api/teachers.api';
import { TeacherResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Avatar } from '../../../shared/ui/avatar';
import { Chip } from '../../../shared/ui/chip';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-admin-teachers',
  imports: [FormsModule, Avatar, Chip, EmptyState, Feedback],
  templateUrl: './admin-teachers.html',
})
export class AdminTeachers {
  private readonly api = inject(TeachersApi);

  readonly teachers = signal<TeacherResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly hiring = signal(false);

  readonly staffNumber = signal('');
  readonly firstName = signal('');
  readonly lastName = signal('');
  readonly email = signal('');

  constructor() {
    this.load();
  }

  private load(): void {
    this.api.list().subscribe({
      next: (teachers) => {
        this.teachers.set(teachers);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  hire(): void {
    this.hiring.set(true);
    this.api
      .hire({
        staffNumber: this.staffNumber(),
        firstName: this.firstName(),
        lastName: this.lastName(),
        email: this.email(),
      })
      .subscribe({
        next: () => {
          this.hiring.set(false);
          this.staffNumber.set('');
          this.firstName.set('');
          this.lastName.set('');
          this.email.set('');
          this.load();
        },
        error: (err) => {
          this.hiring.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  archive(teacher: TeacherResponse): void {
    this.api.archive(teacher.id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
