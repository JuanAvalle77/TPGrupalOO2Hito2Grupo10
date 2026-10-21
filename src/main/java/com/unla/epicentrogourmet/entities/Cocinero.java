package com.unla.epicentrogourmet.entities;

import java.time.LocalDate;

import com.unla.epicentrogourmet.enums.RolCocina;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Personal de cocina: la "tarea especial" es el rol (cocinero, ayudante, lavaplatos). */
@Entity
@Getter @Setter @NoArgsConstructor
public class Cocinero extends Personal {

	@Enumerated(EnumType.STRING)
	private RolCocina rolCocina;

	@Override
	public String getRolDescripcion() {
		return rolCocina != null ? rolCocina.getDescripcion() : "Cocina";
	}

	@Override
	public String getConceptoPlus(LocalDate fechaCalculo) {
		return "Plus por categoría";
	}

	@Override
	public double calcularPlus(CostoFestival costos, LocalDate fechaCalculo) {
		return costos.getPlusCategoria(rolCocina);
	}
}
