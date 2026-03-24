import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { MatchHistoryDTO } from '../../models/match.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  history: MatchHistoryDTO[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';

  constructor(
    private matchService: MatchService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory(): void {
    this.matchService.getHistory().subscribe({
      next: (data) => {
        this.history = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao carregar o histórico de partidas.';
        this.isLoading = false;
      }
    });
  }

  startNewGame(): void {
    this.matchService.startMatch().subscribe({
      next: (res) => {
        // Redirecionará para a tela do jogo passando o ID da partida na URL
        this.router.navigate(['/game', res.matchId]);
      },
      error: () => {
        alert('Erro ao iniciar nova partida. Tente novamente.');
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }
}