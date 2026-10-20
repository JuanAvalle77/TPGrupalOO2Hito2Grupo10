package com.unla.epicentrogourmet.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Unidad disponible para el cierre del cajero en la fecha actual. */
@Getter
@AllArgsConstructor
public class UnidadCajaDTO {
	private final Long id;
	private final String nombre;
	private final String festival;
	private final double totalDelDia;
}
