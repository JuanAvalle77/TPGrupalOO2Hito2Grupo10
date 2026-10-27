package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.RankingUnidadDTO;
import com.unla.epicentrogourmet.dtos.ReporteMayoresCanonDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.UnidadVenta;

public interface IAdminReportService {
	List<UnidadVenta> unidadesDelFestival(Long festivalId);
	List<RankingUnidadDTO> ranking(Long festivalId);
	List<ReporteMayoresCanonDTO> mayoresCanones(Long festivalId, int cantidad);
	Festival obtenerFestival(Long festivalId);
}
