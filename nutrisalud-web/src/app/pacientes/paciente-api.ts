import { Service, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { JSend } from '../core/api/jsend';
import { Pagina } from '../core/api/pagina';
import { Paciente, PacienteDatos } from '../dominio/paciente';
import { ControlPaciente } from '../dominio/control';

@Service()
export class PacienteApi {

  private http = inject(HttpClient);
  private url = `${environment.apiUrl}/pacientes`;

  listarPagina(pagina: number, tamano: number, buscar: string): Observable<Pagina<Paciente>> {
    const params = new HttpParams()
      .set('pagina', pagina)
      .set('tamano', tamano)
      .set('buscar', buscar);
    return this.http.get<JSend<Pagina<Paciente>>>(this.url, { params })
      .pipe(map(respuesta => respuesta.data));
  }

  buscarPorId(id: number): Observable<Paciente> {
    return this.http.get<JSend<Paciente>>(`${this.url}/${id}`)
      .pipe(map(respuesta => respuesta.data));
  }

  listarControles(id: number, pagina: number, tamano: number): Observable<Pagina<ControlPaciente>> {
    const params = new HttpParams()
      .set('pagina', pagina)
      .set('tamano', tamano);
    return this.http.get<JSend<Pagina<ControlPaciente>>>(`${this.url}/${id}/controles`, { params })
      .pipe(map(respuesta => respuesta.data));
  }

  crear(datos: PacienteDatos): Observable<Paciente> {
    return this.http.post<JSend<Paciente>>(this.url, datos)
      .pipe(map(respuesta => respuesta.data));
  }

  actualizar(id: number, datos: PacienteDatos): Observable<Paciente> {
    return this.http.put<JSend<Paciente>>(`${this.url}/${id}`, datos)
      .pipe(map(respuesta => respuesta.data));
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
