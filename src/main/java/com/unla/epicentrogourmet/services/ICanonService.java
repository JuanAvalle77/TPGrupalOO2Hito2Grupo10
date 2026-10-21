package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.CanonUnidadDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface ICanonService {

	/** Monto de canon a pagar por la unidad, segun los costos que fijo su festival. */
	double calcularCanon(UnidadVenta unidad);

	/** Canon de una unidad con el detalle del calculo, listo para mostrar. */
	CanonUnidadDTO calcularDetalle(UnidadVenta unidad);

	List<CanonUnidadDTO> calcularDetalle(List<UnidadVenta> unidades);
}
