import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true; // Permite o acesso à rota
  }

  // Se não estiver logado, chuta o usuário de volta para o login
  router.navigate(['/login']);
  return false;
};