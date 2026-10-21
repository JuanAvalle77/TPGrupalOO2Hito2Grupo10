package com.unla.epicentrogourmet.entities;

import java.time.LocalDate;

import com.unla.epicentrogourmet.enums.Turno;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Cajero extends Personal {

	@Enumerated(EnumType.STRING)
	private Turno turno;

	@Override
	public String getRolDescripcion() {
		return "Cajero" + (turno != null ? " (turno " + turno.name().toLowerCase() + ")" : "");
	}

	@Override
	public String getConceptoPlus(LocalDate fechaCalculo) {
		return "Antigüedad (" + getAntiguedadEnAnios(fechaCalculo) + " años)";
	}

	@Override
	public double calcularPlus(CostoFestival costos, LocalDate fechaCalculo) {
		return getAntiguedadEnAnios(fechaCalculo) * costos.getPlusAntiguedadAnual();
	}
}
