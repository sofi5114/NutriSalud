package pe.upn.nutrisalud.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.upn.nutrisalud.dto.ControlBajo;
import pe.upn.nutrisalud.model.Control;

public interface ControlRepository extends JpaRepository<Control, Long> {

    // El control más reciente del niño: de él salen hemoglobina y fechaUltimoControl.
    // Si hay dos el mismo día, gana el de id mayor (el último registrado).
    Optional<Control> findFirstByPacienteIdOrderByFechaDescIdDesc(Long pacienteId);

    // S14 · historial por páginas (lo pide el detalle del paciente en Angular)
    Page<Control> findByPacienteId(Long pacienteId, Pageable pageable);

    // S14 · práctica 2: activos cuyo último control tiene hemoglobina < limite
    @Query("""
            SELECT COUNT(DISTINCT p.id)
            FROM Control c
            JOIN c.paciente p
            WHERE p.activo = true
              AND c.hemoglobina < :limite
              AND c.fecha = (SELECT MAX(c2.fecha) FROM Control c2 WHERE c2.paciente = p)
            """)
    long contarAlertas(@Param("limite") BigDecimal limite);

    @Query("""
            SELECT new pe.upn.nutrisalud.dto.ControlBajo(
                       p.id, p.nombres, p.apellidos, c.fecha, c.hemoglobina)
            FROM Control c
            JOIN c.paciente p
            JOIN p.programas pr
            WHERE pr.id = :programaId
              AND p.activo = true
              AND c.hemoglobina < :limite
              AND c.fecha >= :desde
            ORDER BY c.hemoglobina ASC, p.id ASC
            """)
    List<ControlBajo> bajosEnPrograma(@Param("programaId") Long programaId,
                                      @Param("limite") BigDecimal limite,
                                      @Param("desde") LocalDate desde);

    @Query("""
            SELECT new pe.upn.nutrisalud.dto.ControlBajo(
                       p.id, p.nombres, p.apellidos, c.fecha, c.hemoglobina)
            FROM Control c
            JOIN c.paciente p
            JOIN p.factores f
            WHERE f.id = :factorId
              AND p.activo = true
              AND c.hemoglobina < :limite
              AND c.fecha >= :desde
            ORDER BY c.hemoglobina ASC, p.id ASC
            """)
    List<ControlBajo> bajosConFactor(@Param("factorId") Long factorId,
                                     @Param("limite") BigDecimal limite,
                                     @Param("desde") LocalDate desde);
}
