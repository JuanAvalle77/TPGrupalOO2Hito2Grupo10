package test;

import java.util.stream.Collectors;

import datos.Festival;
import datos.FoodTruck;
import datos.Plato;
import datos.UnidadVenta;
import negocio.FestivalABM;
import negocio.UnidadVentaABM;

/**
 * Caso de Uso (Hito 1): FoodTrucks de un festival junto con la cantidad de
 * platos que ofrece cada uno.
 * Combina Herencia (FoodTruck extiende UnidadVenta) y Uno a Muchos
 * (UnidadVenta -> Plato).
 */
public class TestCasoDeUsoFoodTrucks {

	public static void main(String[] args) {

		long idFestival = 1; // ajustar segun el id que haya quedado al correr TestCargarDatosPrueba

		FestivalABM festivalAbm = new FestivalABM();
		UnidadVentaABM abm = new UnidadVentaABM();

		Festival festival = festivalAbm.traer(idFestival);

		System.out.printf("FoodTrucks del festival id=%d%n", idFestival);
		for (UnidadVenta u : abm.traerFoodTrucksDeFestival(festival)) {
			FoodTruck ft = (FoodTruck) u;
			String nombresPlatos = ft.getPlatos().stream().map(Plato::getNombre).collect(Collectors.joining(", "));
			System.out.printf("- %s (codigo=%s, patente=%s) -> %d platos: %s%n", ft.getNombre(), ft.getCodigo(),
					ft.getPatente(), ft.getPlatos().size(), nombresPlatos);
		}
	}
}
