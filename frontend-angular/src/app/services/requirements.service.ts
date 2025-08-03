import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RequirementsService {
  constructor(private http: HttpClient) {}

  analyzeRequirements(requirementsText: string): Observable<any> {
    return this.http.post('/api/requirements/analyze', { requirementsText });
  }
}
