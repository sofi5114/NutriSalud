import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthApi } from '../../auth/auth-api';

@Component({
  selector: 'app-encabezado',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './encabezado.html',
  styleUrl: './encabezado.css'
})
export class Encabezado {

  auth = inject(AuthApi);
  private router = inject(Router);

  salir(): void {
    this.auth.cerrarSesion();
    this.router.navigate(['/login']);
  }
}
