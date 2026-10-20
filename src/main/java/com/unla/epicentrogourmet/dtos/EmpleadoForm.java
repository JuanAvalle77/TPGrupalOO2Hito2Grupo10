package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.enums.Turno;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos del formulario de alta y modificacion de empleados (no persiste).
 * Las reglas que dependen de la base (DNI y email unicos, mayoria de edad, turno o
 * rol segun el tipo) las valida EmpleadoService.
 */
@Getter @Setter @NoArgsConstructor
public class EmpleadoForm {

	/** Solo viene cargado al modificar. */
	private Long id;

	/** Unidad a la que se asigna el empleado (el responsable puede tener mas de una). */
	private Long unidadId;

	@NotBlank(message = "Ingresá el nombre.")
	@Size(max = 100, message = "El nombre es demasiado largo.")
	private String nombre;

	@NotBlank(message = "Ingresá el apellido.")
	@Size(max = 100, message = "El apellido es demasiado largo.")
	private String apellido;

	@NotNull(message = "Ingresá el DNI.")
	@Min(value = 1_000_000, message = "El DNI debe tener entre 7 y 8 dígitos.")
	@Max(value = 99_999_999, message = "El DNI debe tener entre 7 y 8 dígitos.")
	private Integer dni;

	@NotNull(message = "Ingresá la fecha de nacimiento.")
	@Past(message = "La fecha de nacimiento debe ser anterior a hoy.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaNacimiento;

	@NotNull(message = "Ingresá la fecha de ingreso.")
	@PastOrPresent(message = "La fecha de ingreso no puede ser futura.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaIngreso;

	@NotBlank(message = "Ingresá el email.")
	@Pattern(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$", message = "El email no tiene un formato válido.")
	@Size(max = 100, message = "El email es demasiado largo.")
	private String email;

	@Size(max = 100, message = "El contacto es demasiado largo.")
	private String contacto;

	@NotNull(message = "Elegí el tipo de empleado.")
	private TipoEmpleado tipo = TipoEmpleado.CAJERO;

	/** Obligatorio si el tipo es CAJERO. */
	private Turno turno;

	/** Obligatorio si el tipo es COCINA. */
	private RolCocina rolCocina;
}
