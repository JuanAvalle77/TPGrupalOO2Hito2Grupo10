package com.unla.epicentrogourmet.entities;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public abstract class Personal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(nullable = false)
	private String apellido;

	@Column(nullable = false, unique = true)
	private int dni;

	@Column(nullable = false)
	private LocalDate fechaNacimiento;

	@Column(nullable = false)
	private LocalDate fechaIngreso;

	/** Email: tambien es el usuario con el que inicia sesion. */
	@Column(nullable = false, unique = true)
	private String email;

	private String contacto;

	/** Baja logica: false = empleado dado de baja (no se borra de la base). */
	private boolean activo = true;

	@ManyToMany(mappedBy = "personal", fetch = FetchType.LAZY)
	private Set<UnidadVenta> unidades = new HashSet<>();

	public int getAntiguedadEnAnios(LocalDate hasta) {
		return Period.between(fechaIngreso, hasta).getYears();
	}

	public boolean esMayorDeEdad() {
		return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= 18;
	}

	public String getNombreCompleto() {
		return apellido + ", " + nombre;
	}
}
