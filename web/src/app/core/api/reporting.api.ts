import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { AtRiskStudentResponse, PromotionSummaryResponse, StudentSummaryResponse } from '../api-models';

@Injectable({ providedIn: 'root' })
export class ReportingApi {
  private readonly http = inject(HttpClient);

  getStudentSummary(studentId: string): Observable<StudentSummaryResponse> {
    return this.http.get<StudentSummaryResponse>(`${API_BASE_URL}/reporting/students/${studentId}/summary`);
  }

  getPromotionSummary(promotionId: string): Observable<PromotionSummaryResponse> {
    return this.http.get<PromotionSummaryResponse>(
      `${API_BASE_URL}/reporting/promotions/${promotionId}/summary`,
    );
  }

  getAtRiskStudents(promotionId: string): Observable<AtRiskStudentResponse[]> {
    return this.http.get<AtRiskStudentResponse[]>(
      `${API_BASE_URL}/reporting/promotions/${promotionId}/at-risk`,
    );
  }
}
