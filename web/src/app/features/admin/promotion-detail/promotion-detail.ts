import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { CoursesApi } from '../../../core/api/courses.api';
import { PromotionsApi } from '../../../core/api/promotions.api';
import { SessionsApi } from '../../../core/api/sessions.api';
import { StudentsApi } from '../../../core/api/students.api';
import { TeachersApi } from '../../../core/api/teachers.api';
import {
  CourseResponse,
  PromotionResponse,
  SessionResponse,
  StudentResponse,
  TeacherResponse,
} from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';

@Component({
  selector: 'app-promotion-detail',
  imports: [DatePipe, FormsModule, RouterLink],
  templateUrl: './promotion-detail.html',
})
export class PromotionDetail {
  private readonly route = inject(ActivatedRoute);
  private readonly promotionsApi = inject(PromotionsApi);
  private readonly studentsApi = inject(StudentsApi);
  private readonly coursesApi = inject(CoursesApi);
  private readonly sessionsApi = inject(SessionsApi);
  private readonly teachersApi = inject(TeachersApi);

  readonly promotionId = this.route.snapshot.paramMap.get('id')!;
  readonly promotion = signal<PromotionResponse | null>(null);
  readonly students = signal<StudentResponse[]>([]);
  readonly courses = signal<CourseResponse[]>([]);
  readonly sessions = signal<SessionResponse[]>([]);
  readonly teachers = signal<TeacherResponse[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly studentNumber = signal('');
  readonly studentFirstName = signal('');
  readonly studentLastName = signal('');
  readonly studentEmail = signal('');
  readonly enrolling = signal(false);

  readonly courseCode = signal('');
  readonly courseTitle = signal('');
  readonly courseCoefficient = signal(1);
  readonly courseTeacherId = signal<string | null>(null);
  readonly creatingCourse = signal(false);

  readonly sessionCourseId = signal<string | null>(null);
  readonly sessionTeacherId = signal('');
  readonly sessionStart = signal('');
  readonly sessionEnd = signal('');
  readonly sessionGraceSeconds = signal(300);
  readonly schedulingSession = signal(false);

  constructor() {
    this.load();
  }

  private load(): void {
    forkJoin({
      promotion: this.promotionsApi.getOne(this.promotionId),
      students: this.promotionsApi.getStudents(this.promotionId),
      courses: this.coursesApi.listByPromotion(this.promotionId),
      sessions: this.sessionsApi.listByPromotion(this.promotionId),
      teachers: this.teachersApi.list(),
    }).subscribe({
      next: ({ promotion, students, courses, sessions, teachers }) => {
        this.promotion.set(promotion);
        this.students.set(students);
        this.courses.set(courses);
        this.sessions.set(sessions);
        this.teachers.set(teachers);
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

  createCourse(): void {
    this.creatingCourse.set(true);
    this.coursesApi
      .create({
        code: this.courseCode(),
        title: this.courseTitle(),
        coefficient: this.courseCoefficient(),
        promotionId: this.promotionId,
        teacherId: this.courseTeacherId(),
      })
      .subscribe({
        next: () => {
          this.creatingCourse.set(false);
          this.courseCode.set('');
          this.courseTitle.set('');
          this.courseTeacherId.set(null);
          this.load();
        },
        error: (err) => {
          this.creatingCourse.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  assignTeacher(course: CourseResponse, teacherId: string): void {
    if (!teacherId) {
      return;
    }
    this.coursesApi.assignTeacher(course.id, teacherId).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  onSessionCourseChange(courseId: string): void {
    this.sessionCourseId.set(courseId || null);
    const course = this.courses().find((c) => c.id === courseId);
    this.sessionTeacherId.set(course?.teacherId ?? '');
  }

  scheduleSession(): void {
    const courseId = this.sessionCourseId();
    if (!courseId) {
      return;
    }
    this.schedulingSession.set(true);
    this.sessionsApi
      .schedule({
        courseId,
        promotionId: this.promotionId,
        teacherId: this.sessionTeacherId(),
        start: new Date(this.sessionStart()).toISOString(),
        end: new Date(this.sessionEnd()).toISOString(),
        gracePeriodSeconds: this.sessionGraceSeconds(),
      })
      .subscribe({
        next: () => {
          this.schedulingSession.set(false);
          this.load();
        },
        error: (err) => {
          this.schedulingSession.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }

  sessionAction(session: SessionResponse, action: 'open' | 'close' | 'cancel'): void {
    const call =
      action === 'open'
        ? this.sessionsApi.open(session.id)
        : action === 'close'
          ? this.sessionsApi.close(session.id)
          : this.sessionsApi.cancel(session.id);
    call.subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  archivePromotion(): void {
    this.promotionsApi.archive(this.promotionId).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }
}
