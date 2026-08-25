import { HttpClient } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { API_BASE_URL } from '../config';
import { AuthClaims, LoginRequest, Role, TokenResponse } from './auth.models';
import { decodeJwt } from './jwt';

const STORAGE_KEY = 'sms.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly claimsSignal = signal<AuthClaims | null>(this.restoreClaims());

  readonly claims = this.claimsSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.claimsSignal() !== null);

  constructor(private readonly http: HttpClient) {}

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${API_BASE_URL}/auth/login`, request).pipe(
      tap((response) => {
        localStorage.setItem(STORAGE_KEY, response.token);
        this.claimsSignal.set(decodeJwt(response.token));
      }),
    );
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.claimsSignal.set(null);
  }

  token(): string | null {
    return localStorage.getItem(STORAGE_KEY);
  }

  hasRole(...roles: Role[]): boolean {
    const claims = this.claimsSignal();
    return claims !== null && roles.includes(claims.role);
  }

  private restoreClaims(): AuthClaims | null {
    const token = localStorage.getItem(STORAGE_KEY);
    if (!token) {
      return null;
    }
    try {
      const claims = decodeJwt(token);
      if (claims.expiresAt <= Date.now()) {
        localStorage.removeItem(STORAGE_KEY);
        return null;
      }
      return claims;
    } catch {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }
}
