import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { CourseResponse } from '../api-models';

export interface CreateCourseRequest {
  code: string;
  title: string;
  coefficient: number;
  promotionId: string;
  teacherId: string | null;
}

@Injectable({ providedIn: 'root' })
export class CoursesApi {
  private readonly http = inject(HttpClient);

  create(request: CreateCourseRequest): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${API_BASE_URL}/courses`, request);
  }

  getOne(id: string): Observable<CourseResponse> {
    return this.http.get<CourseResponse>(`${API_BASE_URL}/courses/${id}`);
  }

  listByPromotion(promotionId: string): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(`${API_BASE_URL}/courses`, { params: { promotionId } });
  }

  assignTeacher(id: string, teacherId: string): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(`${API_BASE_URL}/courses/${id}/assign-teacher`, { teacherId });
  }
}
