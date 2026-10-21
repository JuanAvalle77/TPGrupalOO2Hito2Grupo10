package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Liquidacion del sueldo de un empleado en un festival (reporte, no se persiste).
 * Tambien es la base del recibo de sueldo en PDF.
 */
@Getter
@AllArgsConstructor
public class LiquidacionDTO {

	private final Long personalId;
	private final String empleado;
	private final int dni;
	private final String rol;
	private final boolean activo;
	private final String festival;
	private final long diasFestival;
	private final double sueldoBase;
	private final String conceptoPlus;
	private final double plus;
	/** Sueldo base + plus (antes de aplicar el factor por duracion del festival). */
	private final double subtotal;
	private final double factor;
	private final double total;
}
