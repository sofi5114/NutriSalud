import { Component, Input, OnChanges, OnInit, computed, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { PacienteApi } from '../paciente-api';
import { Paciente } from '../../dominio/paciente';
import { ControlPaciente } from '../../dominio/control';
import { Pagina } from '../../core/api/pagina';

@Component({
  selector: 'app-detalle-paciente',
  imports: [RouterLink, DatePipe, DecimalPipe],
  templateUrl: './detalle-paciente.html',
  styleUrl: './detalle-paciente.css'
})
export class DetallePaciente implements OnInit, OnChanges {

  private api = inject(PacienteApi);
  private router = inject(Router);

  @Input() id = '';
  @Input() pagina?: string;

  readonly tamano = 5;

  paciente = signal<Paciente | null>(null);
  controles = signal<Pagina<ControlPaciente> | null>(null);
  cargando = signal(true);
  error = signal('');

  actual = computed(() => (this.controles()?.pagina ?? 0) + 1);
  total = computed(() => Math.max(this.controles()?.totalPaginas ?? 1, 1));

  ngOnInit(): void {
    this.api.buscarPorId(Number(this.id))
      .subscribe(p => this.paciente.set(p));
  }

  ngOnChanges(): void {
    this.cargarControles();
  }

  cargarControles(): void {
    const numero = Math.max(Number(this.pagina) || 1, 1);
    this.cargando.set(true);
    this.api.listarControles(Number(this.id), numero - 1, this.tamano).subscribe({
      next: c => {
        if (c.pagina > 0 && c.pagina >= c.totalPaginas) {
          this.router.navigate([], {
            queryParams: { pagina: Math.max(c.totalPaginas, 1) },
            queryParamsHandling: 'merge',
            replaceUrl: true,
          });
          return;
        }
        this.controles.set(c);
        this.error.set('');
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el historial de controles');
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

  eliminar(): void {
    if (!confirm('¿Dar de baja a este paciente?')) {
      return;
    }
    this.api.eliminar(Number(this.id))
      .subscribe(() => this.router.navigate(['/pacientes']));
  }
}