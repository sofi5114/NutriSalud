import { Routes } from '@angular/router';
import { Login } from './core/auth/login/login';

const cargarFormularioPaciente = () =>
  import('./pacientes/formulario-paciente/formulario-paciente').then(m => m.FormularioPaciente);

export const routes: Routes = [
  { path: '', redirectTo: 'pacientes', pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: 'pacientes',
    loadComponent: () =>
      import('./pacientes/lista-pacientes/lista-pacientes').then(m => m.ListaPacientes),
  },
  { path: 'pacientes/nuevo', loadComponent: cargarFormularioPaciente },
  {
    path: 'pacientes/:id',
    loadComponent: () =>
      import('./pacientes/detalle-paciente/detalle-paciente').then(m => m.DetallePaciente),
  },
  { path: 'pacientes/:id/editar', loadComponent: cargarFormularioPaciente },
  { path: '**', redirectTo: 'pacientes' },
];
