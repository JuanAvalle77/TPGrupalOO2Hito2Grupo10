package com.unla.epicentrogourmet.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Desglose de rentabilidad neta, no se persiste. Los costos fijos (sueldos y canon) vienen
 * ya prorrateados: costo total x (dias considerados / dias del festival).
 */
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
	/** Dias del periodo evaluado. */
	private final long diasConsiderados;
	/** Duracion total del festival, en dias. */
	private final long diasFestival;
	/** Fraccion de los costos fijos que se imputa al periodo (0 a 1). */
	private final double proporcion;
	/** Resultado de cada dia del periodo, en orden. */
	private final List<RentabilidadDiaDTO> dias;
	/** true si el rango pedido llegaba a dias que todavia no pasaron y se evaluo solo hasta hoy. */
	private final boolean recortadoAHoy;

	public long getDiasRentables() {
		return dias.stream().filter(RentabilidadDiaDTO::isRentable).count();
	}
}
