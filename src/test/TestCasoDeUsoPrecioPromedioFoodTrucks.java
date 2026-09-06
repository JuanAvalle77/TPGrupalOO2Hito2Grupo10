package test;

import datos.Festival;
import negocio.FestivalABM;
import negocio.UnidadVentaABM;

/**
 * Caso de Uso: precio de venta promedio de los platos que ofrecen los
 * FoodTrucks de un festival. Combina Herencia (FoodTruck) + Uno a Muchos
 * (UnidadVenta -> Plato), resolviendo el promedio con un agregado HQL (avg)
 * en vez de calcularlo recorriendo la lista en Java.
 */
public class TestCasoDeUsoPrecioPromedioFoodTrucks {

	public static void main(String[] args) {

		long idFestival = 1; // ajustar segun el id que haya quedado al correr TestCargarDatosPrueba

		FestivalABM festivalAbm = new FestivalABM();
		UnidadVentaABM abm = new UnidadVentaABM();

		Festival festival = festivalAbm.traer(idFestival);

		Double promedio = abm.traerPrecioPromedioPlatosFoodTrucksDeFestival(festival);

		System.out.printf("Precio promedio de los platos de los FoodTrucks del festival id=%d: $%.2f%n", idFestival,
				promedio);
	}
}
