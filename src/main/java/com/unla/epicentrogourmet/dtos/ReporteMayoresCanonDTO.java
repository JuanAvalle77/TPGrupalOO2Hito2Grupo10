package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Unidad ordenada por el canon que paga en un festival; objeto de reporte, no entidad. */
@Getter
@AllArgsConstructor
public class ReporteMayoresCanonDTO {
	private final String nombreComercial;
	private final String codigo;
	private final String tipo;
	private final double canon;
}
