import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PlanResponse {
  userId: string;
  message: string;
  scheduledEvents: string[];
}

@Injectable({ providedIn: 'root' })
export class PlanService {
  private http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api/plan';

  generate(userId: string): Observable<PlanResponse> {
    return this.http.post<PlanResponse>(`${this.baseUrl}/generate`, { userId });
  }
}