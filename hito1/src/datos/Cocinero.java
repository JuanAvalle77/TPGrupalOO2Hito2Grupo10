package datos;

import java.time.LocalDate;

public class Cocinero extends Personal {

	private String especialidad;
	private double plusEspecialidad;

	public Cocinero() {
	}

	public Cocinero(String nombre, String apellido, int dni, LocalDate fechaNacimiento, LocalDate fechaIngreso,
			double sueldoBase, String contacto, String especialidad, double plusEspecialidad) {
		super(nombre, apellido, dni, fechaNacimiento, fechaIngreso, sueldoBase, contacto);
		this.especialidad = especialidad;
		this.plusEspecialidad = plusEspecialidad;
	}

	@Override
	public double calcularSueldo() {
		return sueldoBase + plusEspecialidad;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public double getPlusEspecialidad() {
		return plusEspecialidad;
	}

	public void setPlusEspecialidad(double plusEspecialidad) {
		this.plusEspecialidad = plusEspecialidad;
	}
}
