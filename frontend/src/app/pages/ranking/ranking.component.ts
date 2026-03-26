import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatchService } from '../../services/match.service';
import { RankingDTO } from '../../models/match.models';

@Component({
  selector: 'app-ranking',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './ranking.component.html',
  styleUrl: './ranking.component.css'
})
export class RankingComponent implements OnInit {
  ranking: RankingDTO[] = [];
  isLoading: boolean = true;

  constructor(private matchService: MatchService) {}

  ngOnInit(): void {
    this.matchService.getRanking().subscribe({
      next: (data) => {
        this.ranking = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        alert('Erro ao carregar o ranking.');
      }
    });
  }

  // Transforma os segundos do banco de dados em formato de cronômetro
  formatDuration(seconds: number | undefined): string {
    if (seconds === undefined || seconds === null) return '--:--';
    const m = Math.floor(seconds / 60).toString().padStart(2, '0');
    const s = (seconds % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  }
}