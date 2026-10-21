package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Canon de una unidad de venta en su festival (reporte, no se persiste). */
@Getter
@AllArgsConstructor
public class CanonUnidadDTO {

	private final String nombreComercial;
	private final String codigo;
	private final String tipo;
	private final String festival;
	private final double superficie;
	private final String detalleCalculo;
	private final double canon;
}
