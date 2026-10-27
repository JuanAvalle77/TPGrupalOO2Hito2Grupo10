package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Desglose de rentabilidad neta, no se persiste. */
@Getter
@AllArgsConstructor
public class RentabilidadDTO {
	private final String unidad;
	private final String festival;
	private final LocalDate desde;
	private final LocalDate hasta;
	private final double ventas;
	private final double costoPlatos;
	private final double sueldos;
	private final double canon;
	private final double rentabilidadNeta;
}
