import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      this.successMessage = '';
      
      const formData = this.registerForm.value;

      this.authService.register(formData).subscribe({
        next: () => {
          this.successMessage = 'Conta criada com sucesso! Autenticando...';
          
          // DELAY DE 2 SEGUNDOS PARA O USUÁRIO LER E O TOKEN SER PROCESSADO COM CALMA
          setTimeout(() => {
            const loginData = {
              login: formData.email,
              password: formData.password
            };
            
            this.authService.login(loginData).subscribe({
              next: () => {
                this.isLoading = false;
                this.router.navigate(['/dashboard']);
              },
              error: () => {
                this.isLoading = false;
                this.router.navigate(['/login']); 
              }
            });
          }, 2000); // 2000 milissegundos = 2 segundos
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = err.error?.message || 'Erro ao criar conta. Tente novamente ou use outro e-mail.';
        }
      });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }
}