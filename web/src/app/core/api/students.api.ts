import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { StudentResponse } from '../api-models';

export interface EnrollStudentRequest {
  studentNumber: string;
  firstName: string;
  lastName: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class StudentsApi {
  private readonly http = inject(HttpClient);

  enroll(request: EnrollStudentRequest): Observable<StudentResponse> {
    return this.http.post<StudentResponse>(`${API_BASE_URL}/students`, request);
  }

  getOne(id: string): Observable<StudentResponse> {
    return this.http.get<StudentResponse>(`${API_BASE_URL}/students/${id}`);
  }

  archive(id: string): Observable<StudentResponse> {
    return this.http.post<StudentResponse>(`${API_BASE_URL}/students/${id}/archive`, {});
  }
}
