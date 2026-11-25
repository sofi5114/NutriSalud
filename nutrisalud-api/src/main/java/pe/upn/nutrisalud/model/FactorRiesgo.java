package pe.upn.nutrisalud.model;

import jakarta.persistence.*;

@Entity
@Table(name = "factor_riesgo")
public class FactorRiesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60, unique = true)
    private String nombre;

    protected FactorRiesgo() { }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
