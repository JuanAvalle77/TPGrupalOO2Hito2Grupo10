package com.unla.epicentrogourmet.entities;

import com.unla.epicentrogourmet.enums.RolCocina;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Costos que establece cada festival. Encapsula los valores que NO deben ir
 * hardcodeados en los metodos (canon, sueldo base, plus por categoria, etc.).
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class CostoFestival {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private double costoPorM2;
	private double plusConexionElectrica;
	private double costoPorMinutoMontaje;
	private double sueldoBase;
	private double plusAntiguedadAnual;
	private double plusCocinero;
	private double plusCocineroAyudante;
	private double plusLavaplatos;

	public double getPlusCategoria(RolCocina rol) {
		return switch (rol) {
			case COCINERO -> plusCocinero;
			case COCINERO_AYUDANTE -> plusCocineroAyudante;
			case LAVAPLATOS -> plusLavaplatos;
		};
	}
}
