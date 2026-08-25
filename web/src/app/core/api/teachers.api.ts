import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { TeacherResponse } from '../api-models';

export interface HireTeacherRequest {
  staffNumber: string;
  firstName: string;
  lastName: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class TeachersApi {
  private readonly http = inject(HttpClient);

  hire(request: HireTeacherRequest): Observable<TeacherResponse> {
    return this.http.post<TeacherResponse>(`${API_BASE_URL}/teachers`, request);
  }

  list(): Observable<TeacherResponse[]> {
    return this.http.get<TeacherResponse[]>(`${API_BASE_URL}/teachers`);
  }

  getOne(id: string): Observable<TeacherResponse> {
    return this.http.get<TeacherResponse>(`${API_BASE_URL}/teachers/${id}`);
  }

  archive(id: string): Observable<TeacherResponse> {
    return this.http.post<TeacherResponse>(`${API_BASE_URL}/teachers/${id}/archive`, {});
  }
}
