import { Service, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { JSend } from '../api/jsend';

export interface Sesion {
  token: string;
  rol: string;
}

const CLAVE_TOKEN = 'nutrisalud.token';

@Service()
export class AuthApi {

  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/auth`;

  login(correo: string, clave: string): Observable<Sesion> {
    return this.http.post<JSend<Sesion>>(`${this.url}/login`, { correo, clave })
      .pipe(
        map(respuesta => respuesta.data),
        tap(sesion => localStorage.setItem(CLAVE_TOKEN, sesion.token))
      );
  }

  token(): string | null {
    return localStorage.getItem(CLAVE_TOKEN);
  }

  haySesion(): boolean {
    return this.token() !== null;
  }

  cerrarSesion(): void {
    localStorage.removeItem(CLAVE_TOKEN);
  }
}
