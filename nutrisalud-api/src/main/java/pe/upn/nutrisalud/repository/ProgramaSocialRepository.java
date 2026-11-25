package pe.upn.nutrisalud.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.nutrisalud.model.ProgramaSocial;

public interface ProgramaSocialRepository
        extends JpaRepository<ProgramaSocial, Long> {
}
