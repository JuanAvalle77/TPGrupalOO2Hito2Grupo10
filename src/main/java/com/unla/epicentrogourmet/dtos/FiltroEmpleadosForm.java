package com.unla.epicentrogourmet.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Filtros combinables para buscar empleados activos de las unidades del responsable. */
@Getter @Setter @NoArgsConstructor
public class FiltroEmpleadosForm {
	@Size(max = 100, message = "El nombre o apellido no puede superar 100 caracteres")
	private String nombreApellido;

	@Pattern(regexp = "^$|^[0-9]{1,8}$", message = "El DNI debe contener solo números y hasta 8 dígitos")
	private String dni;

	private String tipo;
	private String detalle;
}
