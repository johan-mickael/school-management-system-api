import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { UsersApi } from '../../../core/api/users.api';
import { Role } from '../../../core/auth/auth.models';
import { UserResponse } from '../../../core/api-models';
import { errorMessage } from '../../../shared/api-error';
import { Chip } from '../../../shared/ui/chip';
import { Feedback } from '../../../shared/ui/feedback';

@Component({
  selector: 'app-admin-users',
  imports: [FormsModule, Chip, Feedback],
  templateUrl: './admin-users.html',
})
export class AdminUsers {
  private readonly api = inject(UsersApi);

  readonly username = signal('');
  readonly password = signal('');
  readonly role = signal<Role>('STUDENT');
  readonly personId = signal('');
  readonly registering = signal(false);
  readonly error = signal<string | null>(null);
  readonly registered = signal<UserResponse[]>([]);

  register(): void {
    this.registering.set(true);
    this.error.set(null);
    this.api
      .register({
        username: this.username(),
        password: this.password(),
        role: this.role(),
        personId: this.role() === 'ADMIN' ? null : this.personId() || null,
      })
      .subscribe({
        next: (user) => {
          this.registering.set(false);
          this.registered.update((list) => [user, ...list]);
          this.username.set('');
          this.password.set('');
          this.personId.set('');
        },
        error: (err) => {
          this.registering.set(false);
          this.error.set(errorMessage(err));
        },
      });
  }
}
