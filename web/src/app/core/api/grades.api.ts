import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import {
  GradeResponse,
  PromotionRankingEntryResponse,
  StudentAveragesResponse,
} from '../api-models';

export interface RecordGradeRequest {
  studentId: string;
  examId: string | null;
  score: number;
  coefficient: number;
}

@Injectable({ providedIn: 'root' })
export class GradesApi {
  private readonly http = inject(HttpClient);

  record(courseId: string, request: RecordGradeRequest): Observable<GradeResponse> {
    return this.http.post<GradeResponse>(`${API_BASE_URL}/courses/${courseId}/grades`, request);
  }

  correct(id: string, score: number): Observable<GradeResponse> {
    return this.http.post<GradeResponse>(`${API_BASE_URL}/grades/${id}/correct`, { score });
  }

  getStudentGrades(studentId: string): Observable<GradeResponse[]> {
    return this.http.get<GradeResponse[]>(`${API_BASE_URL}/students/${studentId}/grades`);
  }

  getStudentAverages(studentId: string): Observable<StudentAveragesResponse> {
    return this.http.get<StudentAveragesResponse>(`${API_BASE_URL}/students/${studentId}/averages`);
  }

  getPromotionRankings(promotionId: string): Observable<PromotionRankingEntryResponse[]> {
    return this.http.get<PromotionRankingEntryResponse[]>(
      `${API_BASE_URL}/promotions/${promotionId}/rankings`,
    );
  }
}
