import { AuthClaims, Role } from './auth.models';

export function decodeJwt(token: string): AuthClaims {
  const payload = token.split('.')[1];
  const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
  const json = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
      .join(''),
  );
  const claims = JSON.parse(json);

  return {
    userId: claims.sub,
    username: claims.username,
    role: claims.role as Role,
    personId: claims.personId ?? null,
    expiresAt: claims.exp * 1000,
  };
}
