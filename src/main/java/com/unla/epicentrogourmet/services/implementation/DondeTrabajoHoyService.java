package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.AsignacionHoyDTO;
import com.unla.epicentrogourmet.entities.Personal;

/** Busca los festivales vigentes en las unidades asignadas al usuario actual. */
@Service
public class DondeTrabajoHoyService {
	private final SesionService sesionService;

	public DondeTrabajoHoyService(SesionService sesionService) {
		this.sesionService = sesionService;
	}

	@Transactional(readOnly = true)
	public List<AsignacionHoyDTO> buscarAsignacionesDeHoy() {
		Personal personal = sesionService.getPersonalActual();
		LocalDate hoy = LocalDate.now();
		return personal.getUnidades().stream()
				.filter(unidad -> unidad.getFestival() != null && unidad.getFestival().estaActivo(hoy))
				.map(unidad -> new AsignacionHoyDTO(unidad.getNombreComercial(), unidad.getCodigo(),
						unidad.getFestival().getNombre(), unidad.getFestival().getFechaInicio(), unidad.getFestival().getFechaFin()))
				.sorted((a, b) -> a.getUnidad().compareToIgnoreCase(b.getUnidad()))
				.toList();
	}
}
