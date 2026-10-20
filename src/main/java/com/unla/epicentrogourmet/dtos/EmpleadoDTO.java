package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Fila de la lista de empleados (no persiste). */
@Getter
@RequiredArgsConstructor
public class EmpleadoDTO {

	private final Long id;
	private final String nombreCompleto;
	private final int dni;
	private final String email;
	private final String contacto;
	private final String tipo;
	/** Turno (cajero) o rol (cocina), ya como texto para mostrar. */
	private final String detalle;
	private final LocalDate fechaIngreso;
	private final int antiguedadAnios;
	private final String unidad;
}
