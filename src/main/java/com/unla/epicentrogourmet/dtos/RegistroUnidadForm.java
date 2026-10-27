package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.enums.Turno;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/** Datos del formulario público para registrar una unidad y su responsable. */
@Getter @Setter @NoArgsConstructor
public class RegistroUnidadForm {

	@NotNull(message = "Seleccioná un festival")
	private Long festivalId;

	@NotBlank(message = "Seleccioná el tipo de unidad")
	private String tipoUnidad;

	@NotBlank(message = "Ingresá el nombre comercial")
	@Size(max = 120, message = "El nombre comercial no puede superar 120 caracteres")
	private String nombreComercial;

	@NotNull(message = "Ingresá la superficie")
	@Positive(message = "La superficie debe ser mayor a cero")
	private Double superficie;

	@NotBlank(message = "Ingresá el código de la unidad")
	@Pattern(regexp = "^[A-Z]{2}[0-9]{8}$", message = "El código debe tener 2 letras mayúsculas y 8 dígitos")
	private String codigo;

	private String patente;
	private boolean requiereElectricidad;
	private Integer cantidadCarpas;
	private Integer tiempoMontaje;

	@NotBlank(message = "Ingresá el nombre del responsable")
	private String nombre;

	@NotBlank(message = "Ingresá el apellido del responsable")
	private String apellido;

	@NotNull(message = "Ingresá el DNI")
	@Min(value = 1_000_000, message = "El DNI debe tener entre 7 y 8 dígitos")
	@Max(value = 99_999_999, message = "El DNI debe tener entre 7 y 8 dígitos")
	private Integer dni;

	@NotNull(message = "Ingresá la fecha de nacimiento")
	@Past(message = "La fecha de nacimiento debe ser anterior a hoy")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaNacimiento;

	@NotNull(message = "Ingresá la fecha de ingreso")
	@PastOrPresent(message = "La fecha de ingreso no puede ser futura")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate fechaIngreso;

	@NotBlank(message = "Ingresá el email")
	@Email(message = "Ingresá un email válido")
	@Size(max = 100, message = "El email no puede superar 100 caracteres")
	private String email;

	@Size(max = 100, message = "El contacto no puede superar 100 caracteres")
	private String contacto;

	@NotNull(message = "Seleccioná el tipo de responsable")
	private TipoEmpleado tipoEmpleado;

	private RolCocina rolCocina;
	private Turno turno;

	@NotBlank(message = "Elegí un nombre de usuario")
	@Size(max = 100, message = "El usuario no puede superar 100 caracteres")
	private String username;

	@NotBlank(message = "Elegí una contraseña")
	private String password;
}
