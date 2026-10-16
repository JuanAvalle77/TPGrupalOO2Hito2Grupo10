package com.unla.epicentrogourmet.enums;

public enum Rol {
	ADMIN, RESPONSABLE, EMPLEADO;

	public String getAuthority() {
		return "ROLE_" + name();
	}
}
