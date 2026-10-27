package com.unla.epicentrogourmet.services.implementation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.InformacionEmpleadoDTO;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.services.implementation.SesionService;

@Service
public class InformacionEmpleadoService {
	private final SesionService sesionService;

	public InformacionEmpleadoService(SesionService sesionService) {
		this.sesionService = sesionService;
	}

	@Transactional(readOnly = true)
	public List<InformacionEmpleadoDTO> buscarUnidadesYFestivales() {
		Personal personal = sesionService.getPersonalActual();
		return personal.getUnidades().stream()
				.filter(unidad -> unidad.getFestival() != null)
				.map(unidad -> new InformacionEmpleadoDTO(unidad.getFestival().getNombre(),
						unidad.getFestival().getTemporada(), unidad.getFestival().getFechaInicio(),
						unidad.getFestival().getFechaFin(), unidad.getNombreComercial(), unidad.getCodigo(), unidad.getTipo()))
				.sorted(Comparator.comparing(InformacionEmpleadoDTO::getFechaInicio).reversed()
						.thenComparing(InformacionEmpleadoDTO::getUnidad, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}
}
