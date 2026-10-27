package com.unla.epicentrogourmet.services;

import java.util.List;

import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.dtos.EmpleadoForm;
import com.unla.epicentrogourmet.dtos.FiltroEmpleadosForm;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;

/**
 * Altas, bajas y modificaciones de empleados. Todos los metodos reciben las unidades del
 * responsable logueado: un responsable solo puede tocar empleados de sus propias unidades.
 */
public interface IEmpleadoService {

	/** Empleados de las unidades (sin el responsable): activos o dados de baja. */
	List<EmpleadoDTO> listar(List<UnidadVenta> misUnidades, boolean activos);

	/** Filtra solo empleados activos de las unidades indicadas. */
	List<EmpleadoDTO> filtrar(List<UnidadVenta> misUnidades, FiltroEmpleadosForm filtro);

	/** Datos del empleado para precargar el formulario de modificacion. */
	EmpleadoForm obtenerParaEditar(Long id, List<UnidadVenta> misUnidades);

	/**
	 * Da de alta al empleado, lo asigna a la unidad, le crea un usuario (su email) con una
	 * contrasenia aleatoria y le envia un mail con esos datos. Si el mail falla, no se da de alta.
	 */
	Personal alta(EmpleadoForm form, List<UnidadVenta> misUnidades);

	/** Modifica los datos del empleado (el tipo no se cambia). */
	void modificar(Long id, EmpleadoForm form, List<UnidadVenta> misUnidades);

	/** Baja logica: el empleado queda inactivo y no puede iniciar sesion. */
	void darDeBaja(Long id, List<UnidadVenta> misUnidades);

	/** Vuelve a dar de alta a un empleado dado de baja (en lugar de crear otro con el mismo DNI o email). */
	void reactivar(Long id, List<UnidadVenta> misUnidades);
}
