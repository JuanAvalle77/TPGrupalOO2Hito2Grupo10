package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.PlatoForm;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IPlatoService {
	List<Plato> listar(UnidadVenta unidad);
	void crear(PlatoForm form, List<UnidadVenta> unidades);
}
