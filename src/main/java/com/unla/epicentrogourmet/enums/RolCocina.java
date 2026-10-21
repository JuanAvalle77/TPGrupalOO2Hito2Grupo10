package com.unla.epicentrogourmet.enums;

/** Rol dentro de la cocina. El plus por categoria de cada uno se define en CostoFestival. */
public enum RolCocina {
	COCINERO("Cocinero"), COCINERO_AYUDANTE("Cocinero ayudante"), LAVAPLATOS("Lavaplatos");

	private final String descripcion;

	RolCocina(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
