import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { Role } from './auth.models';
import { AuthService } from './auth.service';

/** Backoffice (ADMIN) lives under its own shell; everyone else lands on the app shell's root. */
export function homePath(role: Role | undefined): string {
  return role === 'ADMIN' ? '/admin' : '/';
}

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }
  return router.parseUrl('/login');
};

export function roleGuard(...roles: Role[]): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.isAuthenticated() && auth.hasRole(...roles)) {
      return true;
    }
    if (!auth.isAuthenticated()) {
      return router.parseUrl('/login');
    }
    return router.parseUrl(homePath(auth.claims()?.role));
  };
}
