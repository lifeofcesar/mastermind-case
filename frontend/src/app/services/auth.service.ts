import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginDTO, RegisterDTO, AuthResponse } from '../models/auth.models';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'mastermind_token';

  constructor(private http: HttpClient, private router: Router) { }

  login(data: LoginDTO): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, data).pipe(
      tap(response => {
        if (response.token) {
          this.setToken(response.token);
        }
      })
    );
  }

  register(data: RegisterDTO): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['/login']);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  // LÊ O NOME DO USUÁRIO DENTRO DO TOKEN JWT
  getUsername(): string {
    const token = this.getToken();
    if (!token) return 'Jogador';
    try {
      // O Payload do JWT é a segunda parte (separada por ponto) codificada em Base64
      const payload = JSON.parse(atob(token.split('.')[1]));
      // O Spring Security guarda o login no "sub" (subject)
      return payload.username || payload.sub || 'Jogador';
    } catch (e) {
      return 'Jogador';
    }
  }
}