package pe.upn.nutrisalud.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.upn.nutrisalud.dto.ControlResumen;
import pe.upn.nutrisalud.dto.PacienteRequest;
import pe.upn.nutrisalud.dto.PacienteResumen;
import pe.upn.nutrisalud.dto.Pagina;
import pe.upn.nutrisalud.exception.RecursoNoEncontradoException;
import pe.upn.nutrisalud.exception.ReglaNegocioException;
import pe.upn.nutrisalud.model.Control;
import pe.upn.nutrisalud.model.Paciente;
import pe.upn.nutrisalud.repository.ControlRepository;
import pe.upn.nutrisalud.repository.PacienteRepository;

@Service
public class PacienteService {

    /** Nadie pide más de 50 filas por página: protege a la base y al navegador. */
    public static final int TAMANO_MAXIMO = 50;

    private final PacienteRepository pacientes;
    private final ControlRepository controles;

    public PacienteService(PacienteRepository pacientes, ControlRepository controles) {
        this.pacientes = pacientes;
        this.controles = controles;
    }

    /**
     * Semana 11: una página de pacientes activos, por apellidos.
     * El "id" al final del orden desempata a los niños con el mismo nombre completo:
     * así nadie se repite ni se pierde entre una página y la siguiente.
     */
    @Transactional(readOnly = true)
    public Pagina<PacienteResumen> listar(int pagina, int tamano, String buscar) {
        int numero = Math.max(pagina, 0);
        int filas = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO);
        Pageable pedido = PageRequest.of(numero, filas, Sort.by("apellidos", "nombres", "id"));
        Page<Paciente> resultado = pacientes.findByActivoTrueAndApellidosContainingIgnoreCase(
                buscar == null ? "" : buscar.trim(), pedido);
        return Pagina.de(resultado.map(this::resumir));
    }

    // S14 · historial de controles, del más reciente al más antiguo
    @Transactional(readOnly = true)
    public Pagina<ControlResumen> listarControles(Long pacienteId, int pagina, int tamano) {
        obtener(pacienteId);                                       // 404 si no existe o fue dado de baja
        int numero = Math.max(pagina, 0);
        int filas = Math.min(Math.max(tamano, 1), TAMANO_MAXIMO);
        Pageable pedido = PageRequest.of(numero, filas, Sort.by(Sort.Direction.DESC, "fecha", "id"));
        Page<Control> resultado = controles.findByPacienteId(pacienteId, pedido);
        return Pagina.de(resultado.map(ControlResumen::de));
    }

    @Transactional(readOnly = true)
    public PacienteResumen buscar(Long id) {
        return resumir(obtener(id));
    }

    /** Devuelve la entidad o lanza 404. La usan otros servicios (Semana 12: ProgramaService). */
    @Transactional(readOnly = true)
    public Paciente obtener(Long id) {
        return pacientes.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente " + id + " no existe"));
    }

    @Transactional
    public PacienteResumen crear(PacienteRequest r) {
        String dni = vacioANulo(r.dni());
        if (dni != null && pacientes.existsByDni(dni)) {
            throw new ReglaNegocioException("Ya existe un paciente con ese DNI");
        }
        Paciente p = new Paciente(r.nombres().trim(), r.apellidos().trim(), dni,
                r.fechaNacimiento(), vacioANulo(r.sexo()), r.altitudMsnm());
        return resumir(pacientes.save(p));
    }

    @Transactional
    public PacienteResumen actualizar(Long id, PacienteRequest r) {
        Paciente p = obtener(id);
        String dni = vacioANulo(r.dni());
        if (dni != null && pacientes.existsByDniAndIdNot(dni, id)) {
            throw new ReglaNegocioException("Ya existe un paciente con ese DNI");
        }
        p.setNombres(r.nombres().trim());
        p.setApellidos(r.apellidos().trim());
        p.setDni(dni);
        p.setFechaNacimiento(r.fechaNacimiento());
        p.setAltitudMsnm(r.altitudMsnm());
        if (r.sexo() != null) {
            p.setSexo(vacioANulo(r.sexo()));
        }
        return resumir(p);              // dentro de la transacción: JPA guarda el cambio al terminar
    }

    /** Baja lógica: la historia clínica no se borra, se desactiva. */
    @Transactional
    public void eliminar(Long id) {
        obtener(id).desactivar();
    }

    @Transactional
    public int corregirAltitud(int anterior, int nueva) {
        if (nueva < 0 || nueva > 5000) {
            throw new ReglaNegocioException("La altitud debe estar entre 0 y 5000 msnm");
        }
        return pacientes.corregirAltitud(anterior, nueva);
    }

    /** Entidad → lo que ve el cliente. Una consulta extra para traer el último control. */
    PacienteResumen resumir(Paciente p) {
        Control ultimo = controles.findFirstByPacienteIdOrderByFechaDescIdDesc(p.getId()).orElse(null);
        return new PacienteResumen(
                p.getId(), p.getDni(), p.getNombres(), p.getApellidos(), p.getFechaNacimiento(),
                p.edadMesesAl(LocalDate.now()), p.getAltitudMsnm(),
                ultimo == null ? null : ultimo.getHemoglobina(),
                ultimo == null ? null : ultimo.getFecha());
    }

    private static String vacioANulo(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
