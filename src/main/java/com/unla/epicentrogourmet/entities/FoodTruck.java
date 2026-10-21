package com.unla.epicentrogourmet.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class FoodTruck extends UnidadVenta {

	private String patente;
	private boolean requiereElectricidad;

	@Override
	public double calcularCanon(CostoFestival costos) {
		double canon = getSuperficie() * costos.getCostoPorM2();
		if (requiereElectricidad) {
			canon += costos.getPlusConexionElectrica();
		}
		return canon;
	}

	@Override
	public String describirCanon(CostoFestival costos) {
		String detalle = String.format("%.1f m² × $%.0f", getSuperficie(), costos.getCostoPorM2());
		if (requiereElectricidad) {
			detalle += String.format(" + $%.0f (conexión eléctrica)", costos.getPlusConexionElectrica());
		}
		return detalle;
	}

	@Override
	public String getTipo() {
		return "Food Truck";
	}
}
