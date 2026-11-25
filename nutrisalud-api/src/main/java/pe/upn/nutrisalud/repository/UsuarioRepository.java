package pe.upn.nutrisalud.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.upn.nutrisalud.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}
