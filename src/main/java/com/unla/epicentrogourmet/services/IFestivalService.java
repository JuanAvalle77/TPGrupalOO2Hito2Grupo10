package com.unla.epicentrogourmet.services;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import com.unla.epicentrogourmet.entities.Festival;

public interface IFestivalService {

	List<Festival> getAll();

	Optional<Festival> findById(Long id);

	/** Rechaza fechas nulas o un fin anterior al inicio. */
	void validarPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
}
