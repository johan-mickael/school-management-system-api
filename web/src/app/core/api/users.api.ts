import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { Role } from '../auth/auth.models';
import { UserResponse } from '../api-models';

export interface RegisterUserRequest {
  username: string;
  password: string;
  role: Role;
  personId: string | null;
}

@Injectable({ providedIn: 'root' })
export class UsersApi {
  private readonly http = inject(HttpClient);

  register(request: RegisterUserRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${API_BASE_URL}/auth/register`, request);
  }
}
