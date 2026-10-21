package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;

import com.unla.epicentrogourmet.entities.Festival;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Datos de un recibo de sueldo, calculados para mostrar o generar PDF. */
@Getter
@AllArgsConstructor
public class ReciboSueldoDTO {
	private final Festival festival;
	private final LiquidacionDTO liquidacion;

	public Long getFestivalId() {
		return festival.getId();
	}

	public String getNombreFestival() {
		return festival.getNombre();
	}

	public LocalDate getFechaInicio() {
		return festival.getFechaInicio();
	}

	public LocalDate getFechaFin() {
		return festival.getFechaFin();
	}
}
