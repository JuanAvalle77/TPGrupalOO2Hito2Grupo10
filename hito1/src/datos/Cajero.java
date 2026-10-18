package datos;

import java.time.LocalDate;

public class Cajero extends Personal {

	private String turno;
	private int plus;

	public Cajero() {
	}

	public Cajero(String nombre, String apellido, int dni, LocalDate fechaNacimiento, LocalDate fechaIngreso,
			double sueldoBase, String contacto, String turno, int plus) {
		super(nombre, apellido, dni, fechaNacimiento, fechaIngreso, sueldoBase, contacto);
		this.turno = turno;
		this.plus = plus;
	}

	@Override
	public double calcularSueldo() {
		return sueldoBase + plus;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public int getPlus() {
		return plus;
	}

	public void setPlus(int plus) {
		this.plus = plus;
	}
}
