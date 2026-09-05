package negocio;

import java.util.List;

import dao.UnidadVentaDao;
import datos.Festival;
import datos.FoodTruck;
import datos.Personal;
import datos.PuestoDesarmable;
import datos.UnidadVenta;

public class UnidadVentaABM {

	UnidadVentaDao dao = new UnidadVentaDao();

	public int agregarFoodTruck(String nombre, float superficie, String codigo, Festival festival,
			Personal responsable, boolean requiereElectricidad, String patente) {
		FoodTruck ft = new FoodTruck(nombre, superficie, codigo, festival, responsable, requiereElectricidad, patente);
		return dao.agregar(ft);
	}

	public int agregarPuestoDesarmable(String nombre, float superficie, String codigo, Festival festival,
			Personal responsable, int cantidadCarpas, int tiempoMontaje) {
		PuestoDesarmable pd = new PuestoDesarmable(nombre, superficie, codigo, festival, responsable, cantidadCarpas,
				tiempoMontaje);
		return dao.agregar(pd);
	}

	public UnidadVenta traer(long idUnidadVenta) {
		return dao.traer(idUnidadVenta);
	}

	public List<UnidadVenta> traer() {
		return dao.traer();
	}

	/**
	 * Caso de Uso ejemplo (Herencia + Uno a Muchos): FoodTrucks de un festival
	 * con la cantidad de platos que ofrece cada uno.
	 */
	public List<UnidadVenta> traerFoodTrucksDeFestival(long idFestival) {
		return dao.traerFoodTrucksDeFestival(idFestival);
	}
	
	// Metodo por Federico Acosta Rosales
	public List<UnidadVenta> traerFoodTrucksConPedidosDeFestival(long idFestival) {
	    return dao.traerFoodTrucksConPedidosDeFestival(idFestival);
	}
	
	/**
	 * Caso de Uso: Puestos Desarmables de un festival junto con la cantidad de
	 * platos que ofrece cada uno (Herencia: PuestoDesarmable · Uno a Muchos:
	 * Festival -> UnidadVenta).
	 */
	public List<UnidadVenta> traerPuestosDesarmablesDeFestival(long idFestival) {
		return dao.traerPuestosDesarmablesDeFestival(idFestival);
	}

	public double superficieTotal(List<UnidadVenta> puestos) {
		double acumulador = 0;
		for (UnidadVenta u : puestos) {
	        PuestoDesarmable pd = (PuestoDesarmable) u;
	        acumulador += pd.getSuperficie();
	    }
		return acumulador;
	}
}
