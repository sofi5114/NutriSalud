package pe.upn.nutrisalud.model;

import jakarta.persistence.*;

/** Cuenta de acceso. La clave se guarda solo como hash BCrypt. */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(nullable = false, length = 15)
    private String estado;         // ACTIVO | INACTIVO

    @ManyToOne                     // por defecto EAGER: el rol llega junto con el usuario
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    protected Usuario() { }

    public boolean estaActivo() {
        return "ACTIVO".equals(estado);
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getEstado() { return estado; }
    public Rol getRol() { return rol; }
}
