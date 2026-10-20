package com.unla.epicentrogourmet.helpers;

/** Constantes de rutas y vistas. Cada caso de uso agrega las suyas aca. */
public class ViewRouteHelper {

	private ViewRouteHelper() {
	}

	// Rutas
	public static final String LOGIN_URL = "/login";
	public static final String ROOT = "/";
	public static final String ADMIN_ROOT = "/admin";
	public static final String RESPONSABLE_ROOT = "/responsable";
	public static final String EMPLEADO_ROOT = "/empleado";

	// Vistas (templates)
	public static final String HOME_INDEX = "home/index";
	public static final String USER_LOGIN = "user/login";
	public static final String ADMIN_INDEX = "admin/index";
	public static final String RESPONSABLE_INDEX = "responsable/index";
	public static final String EMPLEADO_INDEX = "empleado/index";
	public static final String EMPLEADO_CERRAR_CAJA = "empleado/cerrar-caja";
	public static final String EMPLEADO_HOY = "empleado/hoy";
}
