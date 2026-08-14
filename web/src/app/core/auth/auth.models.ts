export type Role = 'ADMIN' | 'TEACHER' | 'STUDENT';

export interface AuthClaims {
  userId: string;
  username: string;
  role: Role;
  personId: string | null;
  expiresAt: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface TokenResponse {
  token: string;
}
