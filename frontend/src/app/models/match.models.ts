export interface MatchHistoryDTO {
  id: string;
  score: number;
  attempts: number;
  status: string;
  date: string;
  durationInSeconds?: number;
}

export interface FeedbackDTO {
  exactMatches: number;
  partialMatches: number;
  matchStatus: string;
  letterStatuses: string[];
}

export interface GuessDTO {
  combination: string[];
}

export interface StartMatchResponse {
  matchId: string;
}

export interface MatchStateResponse {
  matchId: string;
  status: string;
  secondsElapsed: number;
  attemptsHistory: {
    guess: string[];
    feedback: FeedbackDTO;
  }[];
}

// ATUALIZADO: Ranking focado em Speedrun (Tempo, Tentativas e Data)
export interface RankingDTO {
  username: string;
  durationInSeconds: number;
  attempts: number;
  date: string;
}