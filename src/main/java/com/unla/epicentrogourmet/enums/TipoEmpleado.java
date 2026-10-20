package com.unla.epicentrogourmet.enums;

/** Tipo de empleado que se elige en el formulario de alta: determina la subclase de Personal. */
public enum TipoEmpleado {
	CAJERO("Cajero"), COCINA("Cocina");

	private final String descripcion;

	TipoEmpleado(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescripcion() {
		return descripcion;
	}
}
