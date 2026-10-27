package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Recaudación agregada de un festival. */
@Getter
@AllArgsConstructor
public class ReporteVentaDTO {
	private final String festival;
	private final long cantidadPedidos;
	private final double recaudacionTotal;
}
