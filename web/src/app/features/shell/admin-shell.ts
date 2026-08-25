import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';

import { Avatar } from '../../shared/ui/avatar';
import { Chip } from '../../shared/ui/chip';
import { NavIcon, NavIconComponent } from '../../shared/ui/nav-icon';
import { AuthService } from '../../core/auth/auth.service';
import { BreadcrumbService } from '../../core/breadcrumb.service';
import { ThemeService } from '../../core/theme/theme.service';

interface AdminItem {
  path: string;
  label: string;
  icon: NavIcon;
}

interface AdminSection {
  key: string;
  label: string;
  icon: NavIcon;
  /** Where the rail icon navigates to. */
  path: string;
  items: AdminItem[];
}

const SECTIONS: AdminSection[] = [
  { key: 'overview', label: 'Overview', icon: 'dashboard', path: '/admin', items: [] },
  {
    key: 'academic',
    label: 'Academic',
    icon: 'promotions',
    path: '/admin/promotions',
    items: [
      { path: '/admin/promotions', label: 'Promotions', icon: 'promotions' },
      { path: '/admin/archive', label: 'Archive', icon: 'archive' },
    ],
  },
  {
    key: 'people',
    label: 'People',
    icon: 'users',
    path: '/admin/teachers',
    items: [
      { path: '/admin/teachers', label: 'Teachers', icon: 'teachers' },
      { path: '/admin/users', label: 'Users', icon: 'users' },
    ],
  },
  {
    key: 'insights',
    label: 'Insights',
    icon: 'reporting',
    path: '/admin/reporting',
    items: [{ path: '/admin/reporting', label: 'Reporting', icon: 'reporting' }],
  },
];

interface Crumb {
  label: string;
  path?: string;
}

/**
 * GitLab Admin Area-style backoffice console: icon rail for top-level
 * sections, a secondary sidebar for that section's pages, and a breadcrumb
 * strip above the content. Deliberately denser and more "ops console" than
 * the plain UserShell used by students/teachers.
 */
@Component({
  selector: 'app-admin-shell',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Avatar, Chip, NavIconComponent],
  templateUrl: './admin-shell.html',
})
export class AdminShell {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly breadcrumbSvc = inject(BreadcrumbService);
  readonly theme = inject(ThemeService);

  readonly claims = this.auth.claims;
  readonly sections = SECTIONS;

  readonly url = signal(this.router.url);

  constructor() {
    this.router.events
      .pipe(
        filter((e): e is NavigationEnd => e instanceof NavigationEnd),
        takeUntilDestroyed(),
      )
      .subscribe((e) => this.url.set(e.urlAfterRedirects));
  }

  readonly activeSection = computed<AdminSection>(() => {
    const url = this.url();
    return (
      SECTIONS.find((s) => s.items.some((i) => url === i.path || url.startsWith(i.path + '/'))) ??
      SECTIONS[0]
    );
  });

  readonly activeItem = computed<AdminItem | null>(() => {
    const url = this.url();
    return this.activeSection().items.find((i) => url === i.path || url.startsWith(i.path + '/')) ?? null;
  });

  readonly breadcrumbs = computed<Crumb[]>(() => {
    const crumbs: Crumb[] = [{ label: 'Admin', path: '/admin' }];
    const section = this.activeSection();
    if (section.key === 'overview') {
      return crumbs;
    }
    crumbs.push({ label: section.label });
    const item = this.activeItem();
    if (item) {
      crumbs.push({ label: item.label, path: item.path });
    }
    const tail = this.breadcrumbSvc.label();
    if (tail) {
      crumbs.push({ label: tail });
    }
    return crumbs;
  });

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/login');
  }
}
