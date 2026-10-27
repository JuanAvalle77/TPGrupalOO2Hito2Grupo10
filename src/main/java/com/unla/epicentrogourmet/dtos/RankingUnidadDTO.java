package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Fila del ranking de recaudación, no se persiste. */
@Getter
@AllArgsConstructor
public class RankingUnidadDTO {
	private final Long unidadId;
	private final String nombre;
	private final String codigo;
	private final double recaudacion;
}
