export interface MatchHistoryDTO {
  id: string;
  score: number;
  status: string;
  date: string;
}

export interface StartMatchResponse {
  matchId: string;
}