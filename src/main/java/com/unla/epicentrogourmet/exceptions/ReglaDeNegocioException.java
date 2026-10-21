package com.unla.epicentrogourmet.exceptions;

/**
 * Se rompio una regla de carga (por ejemplo, empleado menor de edad o email repetido).
 * Es una RuntimeException, asi que la transaccion del servicio se deshace.
 * Si trae un campo, el controlador muestra el mensaje junto a ese campo del formulario.
 */
public class ReglaDeNegocioException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String campo;

	public ReglaDeNegocioException(String mensaje) {
		this(null, mensaje);
	}

	public ReglaDeNegocioException(String campo, String mensaje) {
		super(mensaje);
		this.campo = campo;
	}

	/** Nombre del campo del formulario al que corresponde el error (puede ser null). */
	public String getCampo() {
		return campo;
	}
}
