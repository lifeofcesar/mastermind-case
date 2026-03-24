import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http'; // Importamos withInterceptors
import { authInterceptor } from './services/auth.interceptor'; // Importamos nosso interceptor

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Registramos o interceptor na cadeia HTTP do Angular
    provideHttpClient(withFetch(), withInterceptors([authInterceptor]))
  ]
};