package datos;

import java.time.LocalDate;
import java.time.Period;

public abstract class Personal {

	protected long idPersonal;
	protected String nombre;
	protected String apellido;
	protected int dni;
	protected LocalDate fechaNacimiento;
	protected LocalDate fechaIngreso;
	protected double sueldoBase;
	protected String contacto;
	protected UnidadVenta unidadVenta;

	public Personal() {
	}

	public Personal(String nombre, String apellido, int dni, LocalDate fechaNacimiento, LocalDate fechaIngreso,
			double sueldoBase, String contacto) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.dni = dni;
		this.fechaNacimiento = fechaNacimiento;
		this.fechaIngreso = fechaIngreso;
		this.sueldoBase = sueldoBase;
		this.contacto = contacto;
	}

	public abstract double calcularSueldo();

	public int getAntiguedadEnAnios() {
		return Period.between(fechaIngreso, LocalDate.now()).getYears();
	}

	public long getIdPersonal() {
		return idPersonal;
	}

	protected void setIdPersonal(long idPersonal) {
		this.idPersonal = idPersonal;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public int getDni() {
		return dni;
	}

	public void setDni(int dni) {
		this.dni = dni;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public double getSueldoBase() {
		return sueldoBase;
	}

	public void setSueldoBase(double sueldoBase) {
		this.sueldoBase = sueldoBase;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public UnidadVenta getUnidadVenta() {
		return unidadVenta;
	}

	public void setUnidadVenta(UnidadVenta unidadVenta) {
		this.unidadVenta = unidadVenta;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [idPersonal=" + idPersonal + ", nombre=" + nombre + ", apellido="
				+ apellido + ", dni=" + dni + "]";
	}
}
