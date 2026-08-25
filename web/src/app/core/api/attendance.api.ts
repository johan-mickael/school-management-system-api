import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE_URL } from '../config';
import { AttendanceRecordResponse } from '../api-models';

@Injectable({ providedIn: 'root' })
export class AttendanceApi {
  private readonly http = inject(HttpClient);

  sign(sessionId: string): Observable<AttendanceRecordResponse> {
    return this.http.post<AttendanceRecordResponse>(
      `${API_BASE_URL}/sessions/${sessionId}/attendance/sign`,
      {},
    );
  }

  justify(sessionId: string, studentId: string, justification: string): Observable<AttendanceRecordResponse> {
    return this.http.post<AttendanceRecordResponse>(
      `${API_BASE_URL}/sessions/${sessionId}/attendance/${studentId}/justify`,
      { justification },
    );
  }

  getSessionAttendance(sessionId: string): Observable<AttendanceRecordResponse[]> {
    return this.http.get<AttendanceRecordResponse[]>(`${API_BASE_URL}/sessions/${sessionId}/attendance`);
  }

  getStudentAttendance(studentId: string): Observable<AttendanceRecordResponse[]> {
    return this.http.get<AttendanceRecordResponse[]>(`${API_BASE_URL}/students/${studentId}/attendance`);
  }
}
