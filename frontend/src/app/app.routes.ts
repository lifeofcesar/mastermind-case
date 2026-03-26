import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { GameComponent } from './pages/game/game.component';
import { RankingComponent } from './pages/ranking/ranking.component';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'game/:id', component: GameComponent, canActivate: [authGuard] },
  { path: 'ranking', component: RankingComponent, canActivate: [authGuard] }, // NOVA ROTA
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];