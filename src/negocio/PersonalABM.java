package negocio;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import dao.PersonalDao;
import datos.Cajero;
import datos.Cocinero;
import datos.Personal;
import datos.UnidadVenta;

public class PersonalABM {

	PersonalDao dao = new PersonalDao();

	private void validarMayorDeEdad(LocalDate fechaNacimiento) {
		if (fechaNacimiento == null || Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18) {
			throw new IllegalArgumentException("El personal del predio debe ser mayor de edad");
		}
	}

	public int agregarCajero(String nombre, String apellido, int dni, LocalDate fechaNacimiento,
			LocalDate fechaIngreso, double sueldoBase, String contacto, String turno, int plus) {
		validarMayorDeEdad(fechaNacimiento);
		Cajero c = new Cajero(nombre, apellido, dni, fechaNacimiento, fechaIngreso, sueldoBase, contacto, turno, plus);
		return dao.agregar(c);
	}

	public int agregarCocinero(String nombre, String apellido, int dni, LocalDate fechaNacimiento,
			LocalDate fechaIngreso, double sueldoBase, String contacto, String especialidad, double plusEspecialidad) {
		validarMayorDeEdad(fechaNacimiento);
		Cocinero c = new Cocinero(nombre, apellido, dni, fechaNacimiento, fechaIngreso, sueldoBase, contacto,
				especialidad, plusEspecialidad);
		return dao.agregar(c);
	}

	/**
	 * Sobrecarga: carga un Cajero ya asignado como staff de una UnidadVenta
	 * (para poder probar el caso de uso de staff de Santiago).
	 */
	public int agregarCajero(String nombre, String apellido, int dni, LocalDate fechaNacimiento,
			LocalDate fechaIngreso, double sueldoBase, String contacto, String turno, int plus,
			UnidadVenta unidadAsignada) {
		validarMayorDeEdad(fechaNacimiento);
		Cajero c = new Cajero(nombre, apellido, dni, fechaNacimiento, fechaIngreso, sueldoBase, contacto, turno, plus);
		c.setUnidadVenta(unidadAsignada);
		return dao.agregar(c);
	}

	/**
	 * Sobrecarga: carga un Cocinero ya asignado como staff de una UnidadVenta.
	 */
	public int agregarCocinero(String nombre, String apellido, int dni, LocalDate fechaNacimiento,
			LocalDate fechaIngreso, double sueldoBase, String contacto, String especialidad, double plusEspecialidad,
			UnidadVenta unidadAsignada) {
		validarMayorDeEdad(fechaNacimiento);
		Cocinero c = new Cocinero(nombre, apellido, dni, fechaNacimiento, fechaIngreso, sueldoBase, contacto,
				especialidad, plusEspecialidad);
		c.setUnidadVenta(unidadAsignada);
		return dao.agregar(c);
	}

	public Personal traer(long idPersonal) {
		return dao.traer(idPersonal);
	}

	public List<Personal> traer() {
		return dao.traer();
	}
	
	/**
	consulta que liste el personal(Cocineros y Cajeros)asignado a una unidad de venta
	puntual,mostrando el sueldo calculado de cada uno.Tiene que combinar la herencia de
	Personal(Cocinero/Cajero) con la relación uno a muchos entre UnidadVenta y
	Personal.
	*/
	
	
   ///Santiago Agarzúa
   public List<Personal> traerPersonalPorRangoEdad(int edadMin, int edadMax) {
       return dao.traerPersonalPorRangoEdad(edadMin, edadMax);
   }
	
	
	
}
