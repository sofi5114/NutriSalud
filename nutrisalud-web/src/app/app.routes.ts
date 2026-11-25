import { Routes } from '@angular/router';
import { Login } from './core/auth/login/login';
import { ListaPacientes } from './pacientes/lista-pacientes/lista-pacientes';
import { DetallePaciente } from './pacientes/detalle-paciente/detalle-paciente';
import { FormularioPaciente } from './pacientes/formulario-paciente/formulario-paciente';

export const routes: Routes = [
  { path: '', redirectTo: 'pacientes', pathMatch: 'full' },
  { path: 'login', component: Login },

  { path: 'pacientes', component: ListaPacientes },
  { path: 'pacientes/nuevo', component: FormularioPaciente },
  { path: 'pacientes/:id', component: DetallePaciente },
  { path: 'pacientes/:id/editar', component: FormularioPaciente },

  { path: '**', redirectTo: 'pacientes' },
];
