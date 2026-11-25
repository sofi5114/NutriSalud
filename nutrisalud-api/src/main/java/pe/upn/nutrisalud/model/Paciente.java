package pe.upn.nutrisalud.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

/** El menor en seguimiento. Nunca se borra: se da de baja con activo = false. */
@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombres;

    @Column(nullable = false, length = 80)
    private String apellidos;

    @Column(length = 8)
    private String dni;                    // null = DNI en trámite

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 1)
    private String sexo;                   // M | F | null

    @Column(name = "altitud_msnm", nullable = false)
    private Integer altitudMsnm;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToMany
    @JoinTable(name = "paciente_programa",
            joinColumns = @JoinColumn(name = "paciente_id"),
            inverseJoinColumns = @JoinColumn(name = "programa_id"))
    private Set<ProgramaSocial> programas = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "paciente_factor",
            joinColumns = @JoinColumn(name = "paciente_id"),
            inverseJoinColumns = @JoinColumn(name = "factor_id"))
    private Set<FactorRiesgo> factores = new HashSet<>();

    protected Paciente() { }

    public Paciente(String nombres, String apellidos, String dni, LocalDate fechaNacimiento,
                    String sexo, Integer altitudMsnm) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.altitudMsnm = altitudMsnm;
    }

    public int edadMesesAl(LocalDate fecha) {
        return (int) Period.between(fechaNacimiento, fecha).toTotalMonths();
    }

    public void desactivar() { this.activo = false; }

    /** Inscribe al niño en un programa. Si ya estaba, el Set no lo repite. */
    public void inscribir(ProgramaSocial programa) {
        programas.add(programa);
    }

    public void agregarFactor(FactorRiesgo factor) {
        factores.add(factor);
    }

    public Long getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
    public Integer getAltitudMsnm() { return altitudMsnm; }
    public boolean isActivo() { return activo; }
    public boolean retirar(ProgramaSocial programa) {
        return programas.removeIf(pr -> pr.getId().equals(programa.getId()));
    }

    public Set<ProgramaSocial> getProgramas() { return programas; }
    public Set<FactorRiesgo> getFactores() { return factores; }

    public void setNombres(String nombres) { this.nombres = nombres; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDni(String dni) { this.dni = dni; }
    public void setFechaNacimiento(LocalDate f) { this.fechaNacimiento = f; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setAltitudMsnm(Integer altitudMsnm) { this.altitudMsnm = altitudMsnm; }
}
