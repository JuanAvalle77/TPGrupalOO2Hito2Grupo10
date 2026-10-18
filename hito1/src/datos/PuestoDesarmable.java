package datos;

public class PuestoDesarmable extends UnidadVenta {

	// Placeholder hasta que la catedra/el grupo defina los valores oficiales de costos.
	private static final double COSTO_POR_M2 = 800.0;
	private static final double COSTO_POR_CARPA = 2000.0;

	private int cantidadCarpas;
	private int tiempoMontaje;

	public PuestoDesarmable() {
	}

	public PuestoDesarmable(String nombre, float superficie, String codigo, Festival festival, Personal responsable,
			int cantidadCarpas, int tiempoMontaje) {
		super(nombre, superficie, codigo, festival, responsable);
		this.cantidadCarpas = cantidadCarpas;
		this.tiempoMontaje = tiempoMontaje;
	}

	@Override
	public double calcularCostoTotal() {
		return (superficie * COSTO_POR_M2) + (cantidadCarpas * COSTO_POR_CARPA);
	}

	public int getCantidadCarpas() {
		return cantidadCarpas;
	}

	public void setCantidadCarpas(int cantidadCarpas) {
		this.cantidadCarpas = cantidadCarpas;
	}

	public int getTiempoMontaje() {
		return tiempoMontaje;
	}

	public void setTiempoMontaje(int tiempoMontaje) {
		this.tiempoMontaje = tiempoMontaje;
	}
}
