package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Unidad y festival activo asignados al empleado para la fecha actual. */
@Getter
@AllArgsConstructor
public class AsignacionHoyDTO {
	private final String unidad;
	private final String codigoUnidad;
	private final String festival;
	private final LocalDate fechaInicio;
	private final LocalDate fechaFin;
}
