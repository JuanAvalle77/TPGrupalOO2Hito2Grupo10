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
	 * Caso de Uso (Herencia + Uno a Muchos): FoodTrucks de un festival con la
	 * cantidad de platos que ofrece cada uno.
	 */
	public List<UnidadVenta> traerFoodTrucksDeFestival(Festival festival) {
		return dao.traerFoodTrucksDeFestival(festival);
	}

	/**
	 * Caso de Uso: precio de venta promedio de los platos de los FoodTrucks
	 * de un festival (agregado resuelto en HQL).
	 */
	public Double traerPrecioPromedioPlatosFoodTrucksDeFestival(Festival festival) {
		return dao.traerPrecioPromedioPlatosFoodTrucksDeFestival(festival);
	}

	// Metodo por Federico Acosta Rosales
	public List<UnidadVenta> traerFoodTrucksConPedidosDeFestival(Festival festival) {
	    return dao.traerFoodTrucksConPedidosDeFestival(festival);
	}
	
	public List<Object[]> facturacionFoodTrucksDeFestival(Festival festival) {
	    return dao.facturacionFoodTrucksDeFestival(festival);
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
