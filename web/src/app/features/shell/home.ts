import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

const HOME_BY_ROLE: Record<string, string> = {
  STUDENT: '/attendance',
  TEACHER: '/teacher/sessions',
  ADMIN: '/admin/promotions',
};

@Component({
  selector: 'app-home',
  template: '',
})
export class Home {
  constructor() {
    const auth = inject(AuthService);
    const router = inject(Router);
    const role = auth.claims()?.role;
    router.navigateByUrl(role ? HOME_BY_ROLE[role] : '/login');
  }
}
