package com.unla.epicentrogourmet.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Liquidacion de todo el personal de una unidad de venta en su festival. */
@Getter
@AllArgsConstructor
public class LiquidacionUnidadDTO {

	private final String unidad;
	private final String festival;
	private final long diasFestival;
	private final double factor;
	private final List<LiquidacionDTO> liquidaciones;
	private final double totalUnidad;
}
