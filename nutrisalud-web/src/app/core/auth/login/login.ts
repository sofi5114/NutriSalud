import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthApi } from '../auth-api';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  private auth = inject(AuthApi);
  private router = inject(Router);

  error = signal('');

  entrar(correo: string, clave: string): void {
    this.auth.login(correo, clave).subscribe({
      next: () => this.router.navigate(['/pacientes']),
      error: () => this.error.set('Correo o clave incorrectos')
    });
  }
}
