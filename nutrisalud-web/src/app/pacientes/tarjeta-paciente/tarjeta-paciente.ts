import { Component, Input, Output, EventEmitter } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Paciente } from '../../dominio/paciente';
import { NivelAnemia, nivelDelPaciente, tieneAnemia } from '../../dominio/anemia';

@Component({
  selector: 'app-tarjeta-paciente',
  imports: [DatePipe, DecimalPipe],
  templateUrl: './tarjeta-paciente.html',
  styleUrl: './tarjeta-paciente.css'
})
export class TarjetaPaciente {
  @Input({ required: true }) paciente!: Paciente;
  @Output() seleccionar = new EventEmitter<Paciente>();

  nivelAnemia(): NivelAnemia {
    return nivelDelPaciente(this.paciente);
  }

  hayAnemia(): boolean {
    return tieneAnemia(this.nivelAnemia());
  }

  avisarSeleccion(): void {
    this.seleccionar.emit(this.paciente);
  }
}
