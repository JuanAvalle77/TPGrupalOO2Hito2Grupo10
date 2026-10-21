package com.unla.epicentrogourmet.helpers;

import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.enums.Turno;

/** Textos para mostrar en pantalla de los enums (se usa tambien desde las vistas con T(...)). */
public class EtiquetasHelper {

	private EtiquetasHelper() {
	}

	public static String turno(Turno turno) {
		if (turno == null) {
			return "-";
		}
		return turno == Turno.MANANA ? "Mañana" : "Noche";
	}

	public static String rolCocina(RolCocina rol) {
		if (rol == null) {
			return "-";
		}
		String texto = rol.name().toLowerCase().replace('_', ' ');
		return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
	}
}
