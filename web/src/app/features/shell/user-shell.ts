import { Component, computed, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { Avatar } from '../../shared/ui/avatar';
import { Chip } from '../../shared/ui/chip';
import { NavIcon, NavIconComponent } from '../../shared/ui/nav-icon';
import { AuthService } from '../../core/auth/auth.service';
import { ThemeService } from '../../core/theme/theme.service';

interface NavLink {
  path: string;
  label: string;
  icon: NavIcon;
}

/**
 * Plain top-nav app shell for STUDENT/TEACHER — a task-focused workspace,
 * deliberately lighter than the admin backoffice console (see AdminShell).
 */
@Component({
  selector: 'app-user-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Avatar, Chip, NavIconComponent],
  templateUrl: './user-shell.html',
})
export class UserShell {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  readonly theme = inject(ThemeService);

  readonly claims = this.auth.claims;

  readonly links = computed<NavLink[]>(() => {
    const role = this.claims()?.role;
    if (role === 'STUDENT') {
      return [
        { path: '/attendance', label: 'Attendance', icon: 'attendance' },
        { path: '/grades', label: 'Grades', icon: 'grades' },
      ];
    }
    if (role === 'TEACHER') {
      return [
        { path: '/teacher/sessions', label: 'Sessions', icon: 'sessions' },
        { path: '/teacher/grading', label: 'Record grades', icon: 'grading' },
        { path: '/teacher/grading/corrections', label: 'Correct grades', icon: 'correct' },
        { path: '/reporting', label: 'Reporting', icon: 'reporting' },
      ];
    }
    return [];
  });

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
