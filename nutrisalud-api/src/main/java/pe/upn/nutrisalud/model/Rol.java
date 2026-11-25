package pe.upn.nutrisalud.model;

import jakarta.persistence.*;

/** Rol del usuario: PERSONAL_DE_SALUD o CIUDADANO (tabla rol). */
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String nombre;

    protected Rol() { }            // lo exige JPA

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
