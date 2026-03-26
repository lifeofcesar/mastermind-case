import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // IMPORTAÇÃO AQUI
import { MatchService } from '../../services/match.service';
import { AuthService } from '../../services/auth.service';
import { MatchHistoryDTO } from '../../models/match.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule], // RouterModule ADICIONADO AQUI!
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  username: string = 'Jogador'; // NOME PERSONALIZADO
  history: MatchHistoryDTO[] = [];
  activeMatch: MatchHistoryDTO | null = null;
  isLoading: boolean = true;
  errorMessage: string = '';

  currentPage: number = 1;
  itemsPerPage: number = 5;

  constructor(private matchService: MatchService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername(); // BUSCA O NOME DO JWT
    this.loadHistory();
  }

  loadHistory(): void {
    this.matchService.getHistory().subscribe({
      next: (data) => { 
        this.history = data; 
        this.activeMatch = this.history.find(m => m.status === 'IN_PROGRESS') || null;
        this.isLoading = false; 
      },
      error: () => { this.errorMessage = 'Erro ao carregar histórico.'; this.isLoading = false; }
    });
  }

  get totalPages(): number {
    return Math.ceil(this.history.length / this.itemsPerPage) || 1;
  }

  get paginatedHistory(): MatchHistoryDTO[] {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    return this.history.slice(startIndex, startIndex + this.itemsPerPage);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }

  prevPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }

  startNewGame(): void {
    if (this.activeMatch) {
      const confirmacao = confirm('Você tem uma partida em andamento. Iniciar uma nova resultará em derrota na atual. Deseja continuar?');
      if (!confirmacao) return;
    }

    this.matchService.startMatch().subscribe({
      next: (res) => this.router.navigate(['/game', res.matchId]),
      error: () => alert('Erro ao iniciar partida.')
    });
  }

  resumeMatch(): void {
    if (this.activeMatch) {
      this.router.navigate(['/game', this.activeMatch.id]);
    }
  }

  formatDuration(seconds: number | undefined): string {
    if (seconds === undefined || seconds === null) return '--:--';
    const m = Math.floor(seconds / 60).toString().padStart(2, '0');
    const s = (seconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }

  logout(): void {
    this.authService.logout();
  }
}