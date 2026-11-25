import { Paciente } from './paciente';

export type NivelAnemia = 'normal' | 'leve' | 'moderada' | 'severa' | 'sin-criterio';

interface Cortes { normal: number; leve: number; moderada: number; }

const CORTES_6_A_23_MESES: Cortes  = { normal: 10.5, leve:  9.5, moderada: 7.0 };
const CORTES_24_A_59_MESES: Cortes = { normal: 11.0, leve: 10.0, moderada: 7.0 };

export function ajustePorAltitud(msnm: number): number {
  if (msnm <= 0) return 0;
  return (0.0056384 * msnm + 0.0000003 * msnm * msnm) / 10;
}

export function hbAjustada(hb: number, msnm: number): number {
  return Math.round((hb - ajustePorAltitud(msnm)) * 10) / 10;
}

export function clasificarAnemia(hb: number, edadMeses: number, msnm: number): NivelAnemia {
  if (edadMeses < 6 || edadMeses > 59) return 'sin-criterio';
  const c = edadMeses <= 23 ? CORTES_6_A_23_MESES : CORTES_24_A_59_MESES;
  const valor = hbAjustada(hb, msnm);
  if (valor >= c.normal)   return 'normal';
  if (valor >= c.leve)     return 'leve';
  if (valor >= c.moderada) return 'moderada';
  return 'severa';
}

export function tieneAnemia(nivel: NivelAnemia): boolean {
  return nivel === 'leve' || nivel === 'moderada' || nivel === 'severa';
}

export function nivelDelPaciente(p: Paciente): NivelAnemia {
  if (p.hemoglobina === null) return 'sin-criterio';
  return clasificarAnemia(p.hemoglobina, p.edadMeses, p.altitudMsnm);
}
