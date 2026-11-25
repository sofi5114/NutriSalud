package pe.upn.nutrisalud.model;

import jakarta.persistence.*;

/** Programa social del Estado (catálogo): Juntos, Cuna Más... */
@Entity
@Table(name = "programa_social")
public class ProgramaSocial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60, unique = true)
    private String nombre;

    protected ProgramaSocial() { }                 // lo exige JPA

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
