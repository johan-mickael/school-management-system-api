import { Routes } from '@angular/router';

import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    title: 'Sign in · School Management',
    loadComponent: () => import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: '',
    loadComponent: () => import('./features/shell/shell').then((m) => m.Shell),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        title: 'Dashboard · School Management',
        loadComponent: () => import('./features/dashboard/dashboard').then((m) => m.Dashboard),
      },
      {
        path: 'attendance',
        title: 'Attendance · School Management',
        canActivate: [roleGuard('STUDENT')],
        loadComponent: () =>
          import('./features/student/attendance/student-attendance').then((m) => m.StudentAttendance),
      },
      {
        path: 'grades',
        title: 'Grades · School Management',
        canActivate: [roleGuard('STUDENT')],
        loadComponent: () => import('./features/student/grades/student-grades').then((m) => m.StudentGrades),
      },
      {
        path: 'teacher/sessions',
        title: 'Sessions · School Management',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () =>
          import('./features/teacher/sessions/teacher-sessions').then((m) => m.TeacherSessions),
      },
      {
        path: 'teacher/sessions/:id',
        title: 'Session attendance · School Management',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () =>
          import('./features/teacher/session-detail/session-detail').then((m) => m.SessionDetail),
      },
      {
        path: 'teacher/grading',
        title: 'Record grades · School Management',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () => import('./features/teacher/grading/teacher-grading').then((m) => m.TeacherGrading),
      },
      {
        path: 'teacher/grading/corrections',
        title: 'Correct grades · School Management',
        canActivate: [roleGuard('TEACHER', 'ADMIN')],
        loadComponent: () =>
          import('./features/teacher/grading-corrections/grading-corrections').then((m) => m.GradingCorrections),
      },
      {
        path: 'admin/promotions',
        title: 'Promotions · School Management',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () =>
          import('./features/admin/promotions/admin-promotions').then((m) => m.AdminPromotions),
      },
      {
        path: 'admin/promotions/:id',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () =>
          import('./features/admin/promotion-shell/promotion-shell').then((m) => m.PromotionShell),
        children: [
          {
            path: '',
            title: 'Promotion overview · School Management',
            loadComponent: () =>
              import('./features/admin/promotion-overview/promotion-overview').then((m) => m.PromotionOverview),
          },
          {
            path: 'students',
            title: 'Promotion students · School Management',
            loadComponent: () =>
              import('./features/admin/promotion-students/promotion-students').then((m) => m.PromotionStudents),
          },
          {
            path: 'courses',
            title: 'Promotion courses · School Management',
            loadComponent: () =>
              import('./features/admin/promotion-courses/promotion-courses').then((m) => m.PromotionCourses),
          },
          {
            path: 'sessions',
            title: 'Promotion sessions · School Management',
            loadComponent: () =>
              import('./features/admin/promotion-sessions/promotion-sessions').then((m) => m.PromotionSessions),
          },
        ],
      },
      {
        path: 'admin/teachers',
        title: 'Teachers · School Management',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () => import('./features/admin/teachers/admin-teachers').then((m) => m.AdminTeachers),
      },
      {
        path: 'admin/users',
        title: 'Users · School Management',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () => import('./features/admin/users/admin-users').then((m) => m.AdminUsers),
      },
      {
        path: 'admin/archive',
        title: 'Archive · School Management',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () =>
          import('./features/admin/archive-promotions/archive-promotions').then((m) => m.ArchivePromotions),
      },
      {
        path: 'admin/archive/:id',
        title: 'Archived promotion · School Management',
        canActivate: [roleGuard('ADMIN')],
        loadComponent: () =>
          import('./features/admin/archive-detail/archive-detail').then((m) => m.ArchiveDetail),
      },
      {
        path: 'reporting',
        title: 'Reporting · School Management',
        canActivate: [roleGuard('ADMIN', 'TEACHER')],
        loadComponent: () => import('./features/reporting/reporting').then((m) => m.Reporting),
      },
      {
        path: 'reporting/student',
        title: 'Student lookup · School Management',
        canActivate: [roleGuard('ADMIN', 'TEACHER')],
        loadComponent: () =>
          import('./features/reporting/student-lookup/student-lookup').then((m) => m.StudentLookup),
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
