import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { MatchHistoryDTO, StartMatchResponse, FeedbackDTO, MatchStateResponse, RankingDTO } from '../models/match.models';

@Injectable({
  providedIn: 'root'
})
export class MatchService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getHistory(): Observable<MatchHistoryDTO[]> {
    return this.http.get<MatchHistoryDTO[]>(`${this.apiUrl}/matches/history`);
  }

  startMatch(): Observable<StartMatchResponse> {
    return this.http.post<StartMatchResponse>(`${this.apiUrl}/matches/start`, {});
  }

  submitGuess(matchId: string, combination: string[]): Observable<FeedbackDTO> {
    return this.http.post<FeedbackDTO>(`${this.apiUrl}/matches/${matchId}/guess`, { combination });
  }

  getMatchState(matchId: string): Observable<MatchStateResponse> {
    return this.http.get<MatchStateResponse>(`${this.apiUrl}/matches/${matchId}`);
  }

  surrenderMatch(matchId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/matches/${matchId}/surrender`, {});
  }

  getRanking(): Observable<RankingDTO[]> {
    return this.http.get<RankingDTO[]>(`${this.apiUrl}/api/ranking`);
  }
}