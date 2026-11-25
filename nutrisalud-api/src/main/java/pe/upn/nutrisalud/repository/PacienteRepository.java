package pe.upn.nutrisalud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.upn.nutrisalud.dto.PacienteAltitud;
import pe.upn.nutrisalud.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Semana 11: una página de los activos cuyo apellido contiene el texto buscado
    Page<Paciente> findByActivoTrueAndApellidosContainingIgnoreCase(String apellidos, Pageable pageable);

    Optional<Paciente> findByIdAndActivoTrue(Long id);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    @Query("""
            SELECT new pe.upn.nutrisalud.dto.PacienteAltitud(
                       p.id, p.nombres, p.apellidos, p.altitudMsnm)
            FROM Paciente p
            JOIN p.programas pr
            WHERE pr.id = :programaId
              AND p.activo = true
              AND p.altitudMsnm BETWEEN :altitudMin AND :altitudMax
            ORDER BY p.altitudMsnm DESC, p.apellidos ASC, p.id ASC
            """)
    List<PacienteAltitud> enProgramaEntreAltitudes(@Param("programaId") Long programaId,
                                                   @Param("altitudMin") int altitudMin,
                                                   @Param("altitudMax") int altitudMax);

    @Modifying
    @Query("UPDATE Paciente p SET p.altitudMsnm = :nueva WHERE p.altitudMsnm = :anterior")
    int corregirAltitud(@Param("anterior") int anterior, @Param("nueva") int nueva);
}
