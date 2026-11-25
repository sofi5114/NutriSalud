export interface Paciente {
  id: number;
  dni: string | null;
  nombres: string;
  apellidos: string;
  fechaNacimiento: string;
  edadMeses: number;
  altitudMsnm: number;
  hemoglobina: number | null;
  fechaUltimoControl: string | null;
}

export interface PacienteDatos {
  dni: string;
  nombres: string;
  apellidos: string;
  fechaNacimiento: string;
  altitudMsnm: number;
}
