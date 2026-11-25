export interface ControlPaciente {
  id: number;
  fecha: string;
  peso: number | null;
  talla: number | null;
  hemoglobina: number | null;
}

export interface ControlDatos {
  fecha: string;
  peso: number;
  talla: number;
  hemoglobina: number;
}
