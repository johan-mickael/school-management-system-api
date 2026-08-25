import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { PromotionResponse, StudentResponse } from '../api-models';

export interface CreatePromotionRequest {
  name: string;
  academicYear: string;
  capacity: number;
}

@Injectable({ providedIn: 'root' })
export class PromotionsApi {
  private readonly http = inject(HttpClient);

  create(request: CreatePromotionRequest): Observable<PromotionResponse> {
    return this.http.post<PromotionResponse>(`${API_BASE_URL}/promotions`, request);
  }

  listActive(): Observable<PromotionResponse[]> {
    return this.http.get<PromotionResponse[]>(`${API_BASE_URL}/promotions`);
  }

  getOne(id: string): Observable<PromotionResponse> {
    return this.http.get<PromotionResponse>(`${API_BASE_URL}/promotions/${id}`);
  }

  getStudents(id: string): Observable<StudentResponse[]> {
    return this.http.get<StudentResponse[]>(`${API_BASE_URL}/promotions/${id}/students`);
  }

  admitStudent(id: string, studentId: string): Observable<StudentResponse> {
    return this.http.post<StudentResponse>(`${API_BASE_URL}/promotions/${id}/students`, { studentId });
  }

  archive(id: string): Observable<PromotionResponse> {
    return this.http.post<PromotionResponse>(`${API_BASE_URL}/promotions/${id}/archive`, {});
  }
}
