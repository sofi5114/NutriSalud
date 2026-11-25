package pe.upn.nutrisalud.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

/** Control de crecimiento y hemoglobina. Solo lo registra el personal de salud. */
@Entity
@Table(name = "control")
public class Control {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(precision = 5, scale = 2)
    private BigDecimal peso;               // kg

    @Column(precision = 5, scale = 2)
    private BigDecimal talla;              // cm

    @Column(precision = 4, scale = 2)
    private BigDecimal hemoglobina;        // g/dL

    protected Control() { }                 // ya estaba: lo usa JPA

    public Control(Paciente paciente, LocalDate fecha, BigDecimal peso,
                   BigDecimal talla, BigDecimal hemoglobina) {
        this.paciente = paciente;
        this.fecha = fecha;
        this.peso = peso;
        this.talla = talla;
        this.hemoglobina = hemoglobina;
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public LocalDate getFecha() { return fecha; }
    public BigDecimal getPeso() { return peso; }
    public BigDecimal getTalla() { return talla; }
    public BigDecimal getHemoglobina() { return hemoglobina; }
}
