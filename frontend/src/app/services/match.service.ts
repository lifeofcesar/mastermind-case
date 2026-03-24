import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { MatchHistoryDTO, StartMatchResponse } from '../models/match.models';

@Injectable({
  providedIn: 'root'
})
export class MatchService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getHistory(): Observable<MatchHistoryDTO[]> {
    return this.http.get<MatchHistoryDTO[]>(`${this.apiUrl}/api/history`);
  }

  startMatch(): Observable<StartMatchResponse> {
    return this.http.post<StartMatchResponse>(`${this.apiUrl}/matches/start`, {});
  }
}