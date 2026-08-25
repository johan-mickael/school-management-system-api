import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { PromotionsApi } from '../../../core/api/promotions.api';
import { StudentsApi } from '../../../core/api/students.api';
import { StudentResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Avatar } from '../../../shared/ui/avatar';
import { Chip } from '../../../shared/ui/chip';
import { ConfirmDialogService } from '../../../shared/ui/confirm-dialog.service';
import { EmptyState } from '../../../shared/ui/empty-state';
import { Feedback } from '../../../shared/ui/feedback';
import { NavIconComponent } from '../../../shared/ui/nav-icon';

@Component({
  selector: 'app-promotion-students',
  imports: [FormsModule, Avatar, Chip, EmptyState, Feedback, NavIconComponent],
  templateUrl: './promotion-students.html',
})
export class PromotionStudents {
  private readonly route = inject(ActivatedRoute);
  private readonly confirm = inject(ConfirmDialogService);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly studentsApi = inject(StudentsApi);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly students = signal<StudentResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly enrolling = signal(false);

  readonly studentNumber = signal('');
  readonly studentFirstName = signal('');
  readonly studentLastName = signal('');
  readonly studentEmail = signal('');

  constructor() {
    this.load();
  }

  private load(): void {
    this.promotionsApi.getStudents(this.promotionId).subscribe({
      next: (students) => {
        this.students.set(students);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }

  enrollAndAdmit(): void {
    this.enrolling.set(true);
    this.studentsApi
      .enroll({
        studentNumber: this.studentNumber(),
        firstName: this.studentFirstName(),
        lastName: this.studentLastName(),
        email: this.studentEmail(),
      })
      .subscribe({
        next: (student) => {
          this.promotionsApi.admitStudent(this.promotionId, student.id).subscribe({
            next: () => {
              this.enrolling.set(false);
              this.studentNumber.set('');
              this.studentFirstName.set('');
              this.studentLastName.set('');
              this.studentEmail.set('');
              this.load();
            },
            error: (err) => {
              this.enrolling.set(false);
              this.error.set(errorMessage(err));
            },
          });
        },
        error: (err) => {
          this.enrolling.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  async archive(student: StudentResponse): Promise<void> {
    const ok = await this.confirm.ask({
      title: 'Archive student?',
      message: `${student.firstName} ${student.lastName} will be removed from this promotion's active roster. This can't be undone.`,
      confirmLabel: 'Archive',
      destructive: true,
    });
    if (!ok) {
      return;
    }
    this.studentsApi.archive(student.id).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
