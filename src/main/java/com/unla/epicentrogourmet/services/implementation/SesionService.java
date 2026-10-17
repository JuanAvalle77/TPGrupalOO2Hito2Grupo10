package com.unla.epicentrogourmet.services.implementation;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;

/**
 * Datos del usuario logueado. Los casos de uso de Responsable y Empleado lo usan
 * para saber de quien se trata (por ejemplo, para encontrar "su" unidad de venta).
 */
@Service("sesionService")
public class SesionService {

	private final IUsuarioRepository usuarioRepository;

	public SesionService(IUsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	/** Usuario autenticado en la sesion actual. */
	public Usuario getUsuarioActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			throw new IllegalStateException("No hay un usuario autenticado");
		}
		return usuarioRepository.findByUsername(auth.getName())
				.orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + auth.getName()));
	}

	/** Personal (empleado o responsable) del usuario logueado. El administrador no tiene. */
	public Personal getPersonalActual() {
		Personal personal = getUsuarioActual().getPersonal();
		if (personal == null) {
			throw new IllegalStateException("El usuario actual no tiene personal asociado");
		}
		return personal;
	}
}
