import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { SessionResponse } from '../api-models';

export interface ScheduleSessionRequest {
  courseId: string;
  promotionId: string;
  teacherId: string;
  start: string;
  end: string;
  gracePeriodSeconds: number;
}

@Injectable({ providedIn: 'root' })
export class SessionsApi {
  private readonly http = inject(HttpClient);

  schedule(request: ScheduleSessionRequest): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${API_BASE_URL}/sessions`, request);
  }

  getOne(id: string): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(`${API_BASE_URL}/sessions/${id}`);
  }

  listByPromotion(promotionId: string, date?: string): Observable<SessionResponse[]> {
    const params: Record<string, string> = { promotionId };
    if (date) {
      params['date'] = date;
    }
    return this.http.get<SessionResponse[]>(`${API_BASE_URL}/sessions`, { params });
  }

  open(id: string): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${API_BASE_URL}/sessions/${id}/open`, {});
  }

  close(id: string): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${API_BASE_URL}/sessions/${id}/close`, {});
  }

  cancel(id: string): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(`${API_BASE_URL}/sessions/${id}/cancel`, {});
  }
}
