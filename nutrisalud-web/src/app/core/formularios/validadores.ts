import { AbstractControl, ValidationErrors } from '@angular/forms';

export function fechaNoFutura(control: AbstractControl): ValidationErrors | null {
  if (!control.value) {
    return null;
  }
  const elegida = new Date(control.value + 'T00:00:00');
  return elegida > new Date() ? { fechaFutura: true } : null;
}
