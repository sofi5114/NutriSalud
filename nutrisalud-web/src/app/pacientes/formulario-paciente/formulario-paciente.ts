import { Component, Input, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PacienteApi } from '../paciente-api';

@Component({
  selector: 'app-formulario-paciente',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './formulario-paciente.html',
  styleUrl: './formulario-paciente.css'
})
export class FormularioPaciente implements OnInit {

  private fb = inject(FormBuilder);
  private api = inject(PacienteApi);
  private router = inject(Router);

  @Input() id?: string;

  formulario = this.fb.nonNullable.group({
    dni: [''],
    nombres: [''],
    apellidos: [''],
    fechaNacimiento: [''],
    altitudMsnm: [0],
  });

  get esEdicion(): boolean {
    return this.id !== undefined;
  }

  ngOnInit(): void {
    if (this.esEdicion) {
      this.api.buscarPorId(Number(this.id)).subscribe(p =>
        this.formulario.patchValue({ ...p, dni: p.dni ?? '' }));
    }
  }

  guardar(): void {
    const datos = this.formulario.getRawValue();
    const peticion = this.esEdicion
      ? this.api.actualizar(Number(this.id), datos)
      : this.api.crear(datos);
    peticion.subscribe(() => this.router.navigate(['/pacientes']));
  }
}
