package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Resumen de recaudación y estado del cierre diario del cajero. */
@Getter
@AllArgsConstructor
public class CierreCajaEstadoDTO {
	private final LocalDate fecha;
	private final boolean cerrado;
	private final List<UnidadCajaDTO> unidades;
}
