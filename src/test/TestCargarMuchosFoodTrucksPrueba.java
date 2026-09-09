package test;

import java.time.LocalDate;

import datos.Festival;
import datos.Personal;
import datos.UnidadVenta;
import negocio.DetallesPedidoABM;
import negocio.FestivalABM;
import negocio.PedidoABM;
import negocio.PersonalABM;
import negocio.PlatoABM;
import negocio.UnidadVentaABM;

/**
 * Carga 7 FoodTrucks adicionales (para llegar a 10 en total junto con los 3
 * ya cargados por TestCargarDatosPrueba / TestCargarMasFoodTrucksPrueba), con
 * sus platos y un pedido cada uno, mas 2 empleados adicionales asignados
 * como staff (para llegar a ~10 Personal en total).
 *
 * Requiere haber corrido antes: TestCargarDatosPrueba,
 * TestCargarMasFoodTrucksPrueba, TestCargarStaffPrueba, TestCargarMasDatosPrueba.
 */
public class TestCargarMuchosFoodTrucksPrueba {

	public static void main(String[] args) {

		FestivalABM festivalAbm = new FestivalABM();
		PersonalABM personalAbm = new PersonalABM();
		UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
		PlatoABM platoAbm = new PlatoABM();
		PedidoABM pedidoAbm = new PedidoABM();
		DetallesPedidoABM detallesAbm = new DetallesPedidoABM();

		Festival festival = festivalAbm.traer(1);
		Personal responsable = personalAbm.traer(1); // Juan Perez, el primer Cocinero cargado

		Object[][] nuevos = {
				{ "Waffle World", "FT00000004", "AD111GG", new String[][] {
						{ "Waffle Clasico", "1500", "500" }, { "Waffle con Nutella", "2000", "700" } }, 4, 6 },
				{ "Tacos Locos", "FT00000005", "AD222HH", new String[][] {
						{ "Taco de Carne", "900", "350" }, { "Taco de Pollo", "850", "320" },
						{ "Nachos", "1200", "400" } }, 6, 4, 5 },
				{ "Cafe Sobre Ruedas", "FT00000006", "AD333II", new String[][] {
						{ "Cafe Latte", "1000", "300" }, { "Medialuna", "500", "150" } }, 8, 8 },
				{ "Helados del Barrio", "FT00000007", "AD444JJ", new String[][] {
						{ "Helado 1 Bocha", "1200", "400" }, { "Helado 2 Bochas", "2000", "700" } }, 5, 5 },
				{ "Pizza al Paso", "FT00000008", "AD555KK", new String[][] {
						{ "Pizza Muzzarella", "3000", "1200" }, { "Pizza Napolitana", "3500", "1400" } }, 3, 3 },
				{ "Bondiola Express", "FT00000009", "AD666LL", new String[][] {
						{ "Bondiola al Pan", "3200", "1300" } }, 7 },
				{ "Veggie Truck", "FT00000010", "AD777MM", new String[][] {
						{ "Wrap Vegano", "2200", "800" }, { "Ensalada Quinoa", "2500", "900" } }, 4, 4 },
		};

		for (Object[] fila : nuevos) {
			String nombre = (String) fila[0];
			String codigo = (String) fila[1];
			String patente = (String) fila[2];
			String[][] platos = (String[][]) fila[3];

			long idFoodTruck = unidadVentaAbm.agregarFoodTruck(nombre, 14.0f, codigo, festival, responsable, true,
					patente);
			UnidadVenta foodTruck = unidadVentaAbm.traer(idFoodTruck);

			long idPedido = pedidoAbm.agregar(LocalDate.of(2026, 9, 3), festival, foodTruck);

			for (int i = 0; i < platos.length; i++) {
				double precioVenta = Double.parseDouble(platos[i][1]);
				double costoProduccion = Double.parseDouble(platos[i][2]);
				long idPlato = platoAbm.agregar(platos[i][0], precioVenta, costoProduccion, foodTruck);
				int cantidad = (int) fila[4 + i];
				detallesAbm.agregar(cantidad, platoAbm.traer(idPlato), pedidoAbm.traer(idPedido));
			}

			System.out.printf("FoodTruck creado: %s (id=%d)%n", nombre, idFoodTruck);
		}

		// 2 empleados adicionales, asignados como staff a dos de los nuevos FoodTrucks
		UnidadVenta waffleWorld = buscarPorCodigo(unidadVentaAbm.traerFoodTrucksDeFestival(festival), "FT00000004");
		UnidadVenta tacosLocos = buscarPorCodigo(unidadVentaAbm.traerFoodTrucksDeFestival(festival), "FT00000005");

		long idNahuel = personalAbm.agregarCajero("Nahuel", "Torres", 37444555, LocalDate.of(1997, 6, 18),
				LocalDate.of(2026, 2, 1), 415000, "nahuel@mail.com", "Mañana", 16000, waffleWorld);
		System.out.printf("Cajero creado y asignado a Waffle World, id=%d%n", idNahuel);

		long idCamila = personalAbm.agregarCocinero("Camila", "Rios", 35666777, LocalDate.of(1993, 2, 25),
				LocalDate.of(2025, 11, 1), 470000, "camila@mail.com", "Comida rapida", 30000, tacosLocos);
		System.out.printf("Cocinero creado y asignado a Tacos Locos, id=%d%n", idCamila);

		System.out.println("Carga masiva completada: 10 FoodTrucks y personal adicional listos.");
	}

	private static UnidadVenta buscarPorCodigo(java.util.List<UnidadVenta> lista, String codigo) {
		return lista.stream().filter(u -> u.getCodigo().equals(codigo)).findFirst()
				.orElseThrow(() -> new IllegalStateException("No se encontro la unidad con codigo " + codigo));
	}
}
