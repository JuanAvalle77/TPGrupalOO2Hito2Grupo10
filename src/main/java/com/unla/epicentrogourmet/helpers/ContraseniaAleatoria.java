package com.unla.epicentrogourmet.helpers;

import java.security.SecureRandom;

/** Genera la contrasenia inicial de un empleado (sin caracteres que se confundan al leerla). */
public class ContraseniaAleatoria {

	private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
	private static final int LONGITUD = 10;
	private static final SecureRandom RANDOM = new SecureRandom();

	private ContraseniaAleatoria() {
	}

	public static String generar() {
		StringBuilder sb = new StringBuilder(LONGITUD);
		for (int i = 0; i < LONGITUD; i++) {
			sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
		}
		return sb.toString();
	}
}
