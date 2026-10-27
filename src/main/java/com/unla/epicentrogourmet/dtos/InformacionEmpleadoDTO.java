package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Festival y unidad donde el empleado está asignado. */
@Getter
@AllArgsConstructor
public class InformacionEmpleadoDTO {
	private final String festival;
	private final String temporada;
	private final LocalDate fechaInicio;
	private final LocalDate fechaFin;
	private final String unidad;
	private final String codigoUnidad;
	private final String tipoUnidad;
}
