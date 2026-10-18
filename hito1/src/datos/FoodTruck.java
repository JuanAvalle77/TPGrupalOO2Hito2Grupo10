package datos;

public class FoodTruck extends UnidadVenta {

	// Placeholder hasta que la catedra/el grupo defina los valores oficiales de costos.
	private static final double COSTO_POR_M2 = 1000.0;
	private static final double PLUS_ELECTRICIDAD = 5000.0;

	private boolean requiereElectricidad;
	private String patente;

	public FoodTruck() {
	}

	public FoodTruck(String nombre, float superficie, String codigo, Festival festival, Personal responsable,
			boolean requiereElectricidad, String patente) {
		super(nombre, superficie, codigo, festival, responsable);
		this.requiereElectricidad = requiereElectricidad;
		this.patente = patente;
	}

	@Override
	public double calcularCostoTotal() {
		double costo = superficie * COSTO_POR_M2;
		if (requiereElectricidad) {
			costo += PLUS_ELECTRICIDAD;
		}
		return costo;
	}

	public boolean isRequiereElectricidad() {
		return requiereElectricidad;
	}

	public void setRequiereElectricidad(boolean requiereElectricidad) {
		this.requiereElectricidad = requiereElectricidad;
	}

	public String getPatente() {
		return patente;
	}

	public void setPatente(String patente) {
		this.patente = patente;
	}
}
