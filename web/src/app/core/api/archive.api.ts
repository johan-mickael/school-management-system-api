import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { PromotionResponse } from '../api-models';

@Injectable({ providedIn: 'root' })
export class ArchiveApi {
  private readonly http = inject(HttpClient);

  listPromotions(): Observable<PromotionResponse[]> {
    return this.http.get<PromotionResponse[]>(`${API_BASE_URL}/archive/promotions`);
  }

  getPromotion(id: string): Observable<PromotionResponse> {
    return this.http.get<PromotionResponse>(`${API_BASE_URL}/archive/promotions/${id}`);
  }
}
