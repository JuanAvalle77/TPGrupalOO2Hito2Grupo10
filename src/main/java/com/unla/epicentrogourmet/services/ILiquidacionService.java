package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.LiquidacionDTO;
import com.unla.epicentrogourmet.dtos.LiquidacionUnidadDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface ILiquidacionService {

	/** Factor segun los dias del festival: hasta 15 = 0,5 · de 16 a 30 = 1 · mas de 30 = (dias/15 + 1) x 0,5. */
	double calcularFactor(long diasFestival);

	/** Sueldo total que cobra el empleado por trabajar en el festival. */
	double calcularSueldo(Personal personal, Festival festival);

	/** Liquidacion detallada (sirve tambien para armar el recibo de sueldo). */
	LiquidacionDTO calcularLiquidacion(Personal personal, Festival festival);

	/** Liquidacion de todo el personal asignado a la unidad, en el festival de la unidad. */
	LiquidacionUnidadDTO liquidarUnidad(UnidadVenta unidad);

	List<LiquidacionUnidadDTO> liquidarUnidades(List<UnidadVenta> unidades);
}
