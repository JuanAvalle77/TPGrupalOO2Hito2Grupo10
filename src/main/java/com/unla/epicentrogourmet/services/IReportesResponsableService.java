package com.unla.epicentrogourmet.services;

import java.time.LocalDate;
import java.util.Optional;

import com.unla.epicentrogourmet.dtos.PlatoDTO;
import com.unla.epicentrogourmet.dtos.RentabilidadDTO;
import com.unla.epicentrogourmet.dtos.ReporteVentaDTO;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IReportesResponsableService {
	ReporteVentaDTO reporteRecaudacion(Long festivalId);
	RentabilidadDTO rentabilidad(Long unidadId, LocalDate desde, LocalDate hasta,
			java.util.List<UnidadVenta> unidadesPermitidas);
	Optional<PlatoDTO> platoEstrella(Long unidadId, java.util.List<UnidadVenta> unidadesPermitidas);
}
