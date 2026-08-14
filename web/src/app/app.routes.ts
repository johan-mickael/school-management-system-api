import { Routes } from '@angular/router';

import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: '',
    loadComponent: () => import('./features/shell/shell').then((m) => m.Shell),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/shell/home').then((m) => m.Home),
      },
      {
        path: 'attendance',
        canActivate: [roleGuard('STUDENT')],
        loadComponent: () =>
          import('./features/student/attendance/student-attendance').then((m) => m.StudentAttendance),
      },
      {
        path: 'grades',
        canActivate: [roleGuard('STUDENT')],
        loadComponent: () => import('./features/student/grades/student-grades').then((m) => m.StudentGrades),
      },
      {
        path: 'teacher/sessions',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () =>
          import('./features/teacher/sessions/teacher-sessions').then((m) => m.TeacherSessions),
      },
      {
        path: 'teacher/sessions/:id',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () =>
          import('./features/teacher/session-detail/session-detail').then((m) => m.SessionDetail),
      },
      {
        path: 'teacher/grading',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () => import('./features/teacher/grading/teacher-grading').then((m) => m.TeacherGrading),
      },
      {
        path: 'admin/promotions',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () =>
          import('./features/admin/promotions/admin-promotions').then((m) => m.AdminPromotions),
      },
      {
        path: 'admin/promotions/:id',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () =>
          import('./features/admin/promotion-detail/promotion-detail').then((m) => m.PromotionDetail),
      },
      {
        path: 'admin/teachers',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () => import('./features/admin/teachers/admin-teachers').then((m) => m.AdminTeachers),
      },
      {
        path: 'admin/users',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () => import('./features/admin/users/admin-users').then((m) => m.AdminUsers),
      },
      {
        path: 'reporting',
        canActivate: [roleGuard('ADMIN', 'TEACHER')],
        loadComponent: () => import('./features/reporting/reporting').then((m) => m.Reporting),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
