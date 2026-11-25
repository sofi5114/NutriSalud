import { Component, Input, OnChanges, computed, inject, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { TarjetaPaciente } from '../tarjeta-paciente/tarjeta-paciente';
import { PacienteApi } from '../paciente-api';
import { Paciente } from '../../dominio/paciente';
import { Pagina } from '../../core/api/pagina';
import { nivelDelPaciente, tieneAnemia } from '../../dominio/anemia';

@Component({
  selector: 'app-lista-pacientes',
  imports: [TarjetaPaciente, RouterLink, RouterLinkActive],
  templateUrl: './lista-pacientes.html',
  styleUrl: './lista-pacientes.css'
})
export class ListaPacientes implements OnChanges {

  private api = inject(PacienteApi);
  private router = inject(Router);

  @Input() pagina?: string;
  @Input() buscar?: string;
  @Input() anemia?: string;
  @Input() nivel?: string;

  readonly tamano = 2;
  niveles = ['leve', 'moderada', 'severa'];
  titulo = 'Pacientes en seguimiento';

  datos = signal<Pagina<Paciente> | null>(null);
  cargando = signal(true);
  error = signal('');

  actual = computed(() => (this.datos()?.pagina ?? 0) + 1);
  total = computed(() => Math.max(this.datos()?.totalPaginas ?? 1, 1));

  visibles = computed<Paciente[]>(() => {
    const d = this.datos();
    if (!d) return [];
    return d.contenido
      .filter(p => this.anemia !== 'si' || tieneAnemia(nivelDelPaciente(p)))
      .filter(p => !this.nivel || nivelDelPaciente(p) === this.nivel);
  });

  ngOnChanges(): void {
    this.cargar();
  }

  private cargar(): void {
    const numero = Math.max(Number(this.pagina) || 1, 1);
    this.cargando.set(true);
    this.api.listarPagina(numero - 1, this.tamano, this.buscar ?? '').subscribe({
      next: p => {
        if (p.pagina > 0 && p.pagina >= p.totalPaginas) {
          this.router.navigate([], {
            queryParams: { pagina: Math.max(p.totalPaginas, 1) },
            queryParamsHandling: 'merge',
            replaceUrl: true,
          });
          return;
        }
        this.datos.set(p);
        this.error.set('');
        this.cargando.set(false);
      },
      error: (e: HttpErrorResponse) => {
        this.error.set(this.mensajeDeError(e));
        this.cargando.set(false);
      },
    });
  }

  irA(numero: number): void {
    this.router.navigate([], {
      queryParams: { pagina: numero },
      queryParamsHandling: 'merge',
    });
  }

  buscarApellido(texto: string): void {
    this.router.navigate([], {
      queryParams: { buscar: texto.trim() || null, pagina: null },
      queryParamsHandling: 'merge',
    });
  }

  verDetalle(p: Paciente): void {
    this.router.navigate(['/pacientes', p.id]);
  }

  private mensajeDeError(e: HttpErrorResponse): string {
    if (e.status === 0) return 'No hay conexión con el servidor';
    if (e.status === 400) return 'La página o el tamaño no son válidos';
    return 'Ocurrió un error en el servidor';
  }
}
