import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { FeedbackDTO } from '../../models/match.models';

interface Attempt {
  guess: string[];
  feedback: FeedbackDTO;
}

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent implements OnInit, OnDestroy {
  matchId: string = '';
  availableColors: string[] = ['A', 'B', 'C', 'D', 'E', 'F'];
  
  currentGuess: string[] = ['', '', '', ''];
  attemptsHistory: Attempt[] = [];
  
  matchStatus: string = 'IN_PROGRESS';
  isSubmitting: boolean = false;
  errorMessage: string = '';

  secondsElapsed: number = 0;
  currentScore: number = 1000;
  timerInterval: any;

  constructor(private route: ActivatedRoute, private router: Router, private matchService: MatchService) {}

  ngOnInit(): void {
    this.matchId = this.route.snapshot.paramMap.get('id') || '';
    if (!this.matchId) { this.goToDashboard(); return; }
    
    this.matchService.getMatchState(this.matchId).subscribe({
      next: (state) => {
        this.attemptsHistory = state.attemptsHistory;
        this.matchStatus = state.status;
        this.secondsElapsed = state.secondsElapsed;
        this.updateLiveScore();
        if (this.matchStatus === 'IN_PROGRESS') {
          this.startTimer();
        }
      },
      error: () => this.goToDashboard()
    });
  }

  ngOnDestroy(): void {
    this.stopTimer();
  }

  startTimer(): void {
    this.timerInterval = setInterval(() => {
      this.secondsElapsed++;
      this.updateLiveScore();
    }, 1000);
  }

  stopTimer(): void {
    if (this.timerInterval) clearInterval(this.timerInterval);
  }

  updateLiveScore(): void {
    this.currentScore = Math.max(0, 1000 - (this.attemptsHistory.length * 60) - (this.secondsElapsed * 2));
  }

  formatTime(totalSeconds: number): string {
    const m = Math.floor(totalSeconds / 60).toString().padStart(2, '0');
    const s = (totalSeconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  selectColor(index: number, color: string): void {
    if (this.matchStatus !== 'IN_PROGRESS') return;
    // REGRA CLÁSSICA: Removemos o bloqueio de repetições. A cor entra direto.
    this.currentGuess[index] = color;
  }

  isGuessComplete(): boolean {
    return this.currentGuess.every(c => c !== '');
  }

  submitGuess(): void {
    if (!this.isGuessComplete() || this.isSubmitting) return;

    this.isSubmitting = true;
    this.errorMessage = '';
    const guessToSend = [...this.currentGuess];

    this.matchService.submitGuess(this.matchId, guessToSend).subscribe({
      next: (feedback) => {
        this.isSubmitting = false;
        this.attemptsHistory.push({ guess: guessToSend, feedback: feedback });
        this.matchStatus = feedback.matchStatus;
        this.updateLiveScore();
        
        if (this.matchStatus === 'IN_PROGRESS') {
          this.currentGuess = ['', '', '', ''];
        } else {
          this.stopTimer();
        }
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || 'Erro ao enviar palpite.';
      }
    });
  }

  surrender(): void {
    if(confirm('Tem certeza que deseja desistir desta partida?')) {
      this.matchService.surrenderMatch(this.matchId).subscribe({
        next: () => {
          this.matchStatus = 'LOST';
          this.stopTimer();
        },
        error: () => alert('Erro ao processar desistência.')
      });
    }
  }

  playAgain(): void {
    this.matchService.startMatch().subscribe({
      next: (res) => {
        this.router.navigateByUrl('/', {skipLocationChange: true}).then(() => {
            this.router.navigate(['/game', res.matchId]);
        });
      }
    });
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}