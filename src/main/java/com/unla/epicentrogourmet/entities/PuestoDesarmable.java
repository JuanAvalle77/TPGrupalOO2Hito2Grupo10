package com.unla.epicentrogourmet.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class PuestoDesarmable extends UnidadVenta {

	private int cantidadCarpas;

	/** Minutos que toma el montaje. */
	private int tiempoMontaje;

	@Override
	public double calcularCanon(CostoFestival costos) {
		return getSuperficie() * costos.getCostoPorM2() - tiempoMontaje * costos.getCostoPorMinutoMontaje();
	}

	@Override
	public String describirCanon(CostoFestival costos) {
		return String.format("%.1f m² × $%.0f − %d min de montaje × $%.0f", getSuperficie(), costos.getCostoPorM2(),
				tiempoMontaje, costos.getCostoPorMinutoMontaje());
	}

	@Override
	public String getTipo() {
		return "Puesto Desarmable";
	}
}
