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
	public static final String REGISTRO_ROOT = "/registro";

	// Vistas (templates)
	public static final String HOME_INDEX = "home/index";
	public static final String USER_LOGIN = "user/login";
	public static final String REGISTRO_UNIDAD = "registro/unidad";
	public static final String ADMIN_INDEX = "admin/index";
	public static final String RESPONSABLE_INDEX = "responsable/index";
	public static final String EMPLEADO_INDEX = "empleado/index";
	public static final String EMPLEADO_CERRAR_CAJA = "empleado/cerrar-caja";
	public static final String EMPLEADO_HOY = "empleado/hoy";
	public static final String EMPLEADO_SUELDOS = "empleado/sueldos";
	public static final String RESPONSABLE_PLATOS = "responsable/platos";
	public static final String RESPONSABLE_PLATO_FORM = "responsable/plato-form";
	public static final String ADMIN_FESTIVALES = "admin/festivales";
	public static final String ADMIN_UNIDADES_FESTIVAL = "admin/unidades-festival";
	public static final String ADMIN_RANKING = "admin/ranking";
	public static final String ADMIN_MAYOR_CANON = "admin/mayor-canon";
	public static final String RESPONSABLE_REPORTE_RECAUDACION = "responsable/reporte-recaudacion";
	public static final String RESPONSABLE_RENTABILIDAD = "responsable/rentabilidad";
	public static final String RESPONSABLE_PLATO_ESTRELLA = "responsable/plato-estrella";
	public static final String EMPLEADO_INFORMACION = "empleado/mi-informacion";
}
