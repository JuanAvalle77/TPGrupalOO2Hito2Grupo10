package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Plato más vendido y sus métricas para el reporte de plato estrella. */
@Getter
@AllArgsConstructor
public class PlatoDTO {
	private final String nombre;
	private final double precioVenta;
	private final long cantidadPedidos;
	private final long unidadesVendidas;
}
