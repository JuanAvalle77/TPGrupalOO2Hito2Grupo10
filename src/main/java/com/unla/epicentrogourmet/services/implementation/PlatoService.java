package com.unla.epicentrogourmet.services.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.PlatoForm;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IPlatoRepository;
import com.unla.epicentrogourmet.services.IPlatoService;

@Service
public class PlatoService implements IPlatoService {

	private final IPlatoRepository platoRepository;

	public PlatoService(IPlatoRepository platoRepository) {
		this.platoRepository = platoRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Plato> listar(UnidadVenta unidad) {
		return platoRepository.findByUnidadVentaIdOrderByNombreAsc(unidad.getId());
	}

	@Override
	@Transactional
	public void crear(PlatoForm form, List<UnidadVenta> unidades) {
		if (unidades.isEmpty()) {
			throw new IllegalArgumentException("No tenés unidades de venta a cargo");
		}
		UnidadVenta unidad = unidades.stream().filter(u -> u.getId().equals(form.getUnidadId())).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Esa unidad no está a tu cargo"));
		if (form.getPrecioVenta() <= form.getCostoProduccion()) {
			throw new IllegalArgumentException("El precio de venta debe ser mayor al costo de producción");
		}
		Plato plato = new Plato();
		plato.setNombre(form.getNombre().trim());
		plato.setPrecioVenta(form.getPrecioVenta());
		plato.setCostoProduccion(form.getCostoProduccion());
		plato.setUnidadVenta(unidad);
		platoRepository.save(plato);
	}
}
