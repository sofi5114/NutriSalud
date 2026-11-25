package pe.upn.nutrisalud.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.ControlConFactoresRequest;
import pe.upn.nutrisalud.dto.ControlRequest;
import pe.upn.nutrisalud.exception.ValorSospechosoException;
import pe.upn.nutrisalud.model.Control;
import pe.upn.nutrisalud.model.Paciente;
import pe.upn.nutrisalud.repository.ControlRepository;

@Service
public class ControlService {

    private static final BigDecimal HB_MINIMA = new BigDecimal("5");

    private final PacienteService pacienteService;
    private final FactorService factorService;
    private final ControlRepository controles;

    public ControlService(PacienteService pacienteService,
                          FactorService factorService,
                          ControlRepository controles) {
        this.pacienteService = pacienteService;
        this.factorService = factorService;
        this.controles = controles;
    }

  @Transactional(rollbackFor = ValorSospechosoException.class)
    public Long registrar(Long pacienteId, ControlConFactoresRequest r)
            throws ValorSospechosoException {
        Paciente p = pacienteService.obtener(pacienteId);
        ControlRequest c = r.control();

        Control guardado = controles.save(
                new Control(p, c.fecha(), c.peso(), c.talla(), c.hemoglobina()));

        for (Long factorId : r.factorIds()) {
            p.agregarFactor(factorService.obtenerFactor(factorId));
        }

        if (c.hemoglobina().compareTo(HB_MINIMA) < 0) {
            throw new ValorSospechosoException(
                    "La hemoglobina " + c.hemoglobina()
                            + " g/dL es menor que 5: parece un error de digitación, verifica el dato");
        }
        return guardado.getId();
    }
}
