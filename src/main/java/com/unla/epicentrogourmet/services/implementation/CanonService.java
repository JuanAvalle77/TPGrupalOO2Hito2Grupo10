package com.unla.epicentrogourmet.services.implementation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.dtos.CanonUnidadDTO;
import com.unla.epicentrogourmet.entities.CostoFestival;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.services.ICanonService;

/**
 * Calculo del canon de una unidad. Los valores (costo por m2, plus electrico, costo por
 * minuto de montaje) salen de CostoFestival; la formula de cada tipo de unidad esta en
 * UnidadVenta.calcularCanon (FoodTruck y PuestoDesarmable).
 */
@Service("canonService")
public class CanonService implements ICanonService {

	@Override
	public double calcularCanon(UnidadVenta unidad) {
		return unidad.calcularCanon(obtenerCostos(unidad));
	}

	@Override
	public CanonUnidadDTO calcularDetalle(UnidadVenta unidad) {
		CostoFestival costos = obtenerCostos(unidad);
		return new CanonUnidadDTO(unidad.getNombreComercial(), unidad.getCodigo(), unidad.getTipo(),
				unidad.getFestival().getNombre(), unidad.getSuperficie(), unidad.describirCanon(costos),
				unidad.calcularCanon(costos));
	}

	@Override
	public List<CanonUnidadDTO> calcularDetalle(List<UnidadVenta> unidades) {
		return unidades.stream().map(this::calcularDetalle).toList();
	}

	private CostoFestival obtenerCostos(UnidadVenta unidad) {
		CostoFestival costos = unidad.getFestival().getCostos();
		if (costos == null) {
			throw new IllegalStateException(
					"El festival '" + unidad.getFestival().getNombre() + "' no tiene costos cargados");
		}
		return costos;
	}
}
