package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Resultado de un dia de la rentabilidad neta, no se persiste. Los sueldos y el canon del
 * dia son la parte diaria de los costos del festival (costo total / dias del festival).
 */
@Getter
@AllArgsConstructor
public class RentabilidadDiaDTO {
	private final LocalDate fecha;
	private final double ventas;
	private final double costoPlatos;
	private final double sueldos;
	private final double canon;
	private final double resultado;

	/** El dia fue rentable si las ventas cubrieron los platos, los sueldos y el canon de ese dia. */
	public boolean isRentable() {
		return resultado > 0;
	}
}
