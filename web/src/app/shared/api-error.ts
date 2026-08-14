import { HttpErrorResponse } from '@angular/common/http';

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors: Record<string, string> | null;
}

export function errorMessage(error: unknown): string {
  if (error instanceof HttpErrorResponse) {
    const body = error.error as ApiError | undefined;
    if (body?.message) {
      return body.message;
    }
    if (body?.fieldErrors) {
      return Object.values(body.fieldErrors).join(', ');
    }
    return `Request failed (${error.status})`;
  }
  return 'Unexpected error';
}
