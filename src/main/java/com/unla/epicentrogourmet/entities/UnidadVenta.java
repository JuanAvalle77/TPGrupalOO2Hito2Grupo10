package com.unla.epicentrogourmet.entities;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter @Setter @NoArgsConstructor
public abstract class UnidadVenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombreComercial;

	/** Superficie en m2 (areaSup). */
	@Column(nullable = false)
	@Positive(message = "La superficie debe ser mayor a cero")
	private double superficie;

	/** Codigo unico de 10 caracteres: 2 letras mayusculas + 8 digitos (ej: FT00000001). */
	@Pattern(regexp = "^[A-Z]{2}[0-9]{8}$", message = "El codigo debe tener 2 letras mayusculas y 8 digitos")
	@Column(nullable = false, unique = true, length = 10)
	private String codigo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "festival_id")
	private Festival festival;

	/** Miembro del staff a cargo de la unidad. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "responsable_id")
	private Personal responsable;

	/** Personal asignado a la unidad (staff). */
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "unidad_personal", joinColumns = @JoinColumn(name = "unidad_id"),
			inverseJoinColumns = @JoinColumn(name = "personal_id"))
	private Set<Personal> personal = new HashSet<>();

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	private Set<Plato> platos = new HashSet<>();

	@OneToMany(mappedBy = "unidadVenta", fetch = FetchType.LAZY)
	private Set<Pedido> pedidosRealizados = new HashSet<>();

	/** Monto a pagar por la unidad, segun los costos que fijo el festival. */
	public abstract double calcularCanon(CostoFestival costos);

	/** Explica como se calculo el canon (para mostrar en pantalla). */
	public abstract String describirCanon(CostoFestival costos);

	/** Tipo legible de la unidad (para reportes). */
	public abstract String getTipo();
}
