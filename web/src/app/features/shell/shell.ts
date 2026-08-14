import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { Avatar } from '../../shared/ui/avatar';
import { Chip } from '../../shared/ui/chip';
import { AuthService } from '../../core/auth/auth.service';

interface NavLink {
  path: string;
  label: string;
}

@Component({
  selector: 'app-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Avatar, Chip],
  templateUrl: './shell.html',
})
export class Shell {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly claims = this.auth.claims;

  readonly links = computed<NavLink[]>(() => {
    const role = this.claims()?.role;
    if (role === 'STUDENT') {
      return [
        { path: '/attendance', label: 'Attendance' },
        { path: '/grades', label: 'Grades' },
      ];
    }
    if (role === 'TEACHER') {
      return [
        { path: '/teacher/sessions', label: 'Sessions' },
        { path: '/teacher/grading', label: 'Grading' },
        { path: '/reporting', label: 'Reporting' },
      ];
    }
    if (role === 'ADMIN') {
      return [
        { path: '/admin/promotions', label: 'Promotions' },
        { path: '/admin/teachers', label: 'Teachers' },
        { path: '/admin/users', label: 'Users' },
        { path: '/reporting', label: 'Reporting' },
      ];
    }
    return [];
  });

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
