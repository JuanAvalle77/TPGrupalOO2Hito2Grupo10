package com.unla.epicentrogourmet.services.implementation;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;

/**
 * Datos del usuario logueado. Los casos de uso de Responsable y Empleado lo usan
 * para saber de quien se trata (por ejemplo, para encontrar "su" unidad de venta).
 */
@Service("sesionService")
public class SesionService {

	private final IUsuarioRepository usuarioRepository;
	private final IUnidadVentaRepository unidadVentaRepository;

	public SesionService(IUsuarioRepository usuarioRepository, IUnidadVentaRepository unidadVentaRepository) {
		this.usuarioRepository = usuarioRepository;
		this.unidadVentaRepository = unidadVentaRepository;
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
		return (Personal) Hibernate.unproxy(personal);
	}

	/** Unidades de venta de las que el usuario logueado es responsable. */
	public List<UnidadVenta> getUnidadesDelResponsable() {
		return unidadVentaRepository.findByResponsableId(getPersonalActual().getId());
	}
}
