package test;

import java.time.LocalDate;
import java.util.List;

import datos.Festival;
import datos.Personal;
import datos.Plato;
import datos.UnidadVenta;
import negocio.DetallesPedidoABM;
import negocio.FestivalABM;
import negocio.PedidoABM;
import negocio.PersonalABM;
import negocio.PlatoABM;
import negocio.UnidadVentaABM;

/**
 * Carga TODOS los datos de prueba en un solo paso: el festival, los 10
 * FoodTrucks, el Puesto Desarmable, el personal (staff), los platos y los
 * pedidos con sus detalles. Correr una sola vez, sobre una base recien
 * creada (DROP DATABASE + CREATE DATABASE).
 */
public class TestCargarTodosLosDatosPrueba {

	public static void main(String[] args) {

		// --- Festival, primer FoodTruck (El Fueguito), sus platos y pedido ---
		{
			FestivalABM festivalAbm = new FestivalABM();
			PersonalABM personalAbm = new PersonalABM();
			UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
			PlatoABM platoAbm = new PlatoABM();
			PedidoABM pedidoAbm = new PedidoABM();
			DetallesPedidoABM detallesAbm = new DetallesPedidoABM();

			long idFestival = festivalAbm.agregar("Fiesta del Choripán", "Otoño",
					LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5));
			System.out.printf("Festival creado, id=%d%n", idFestival);

			long idCocinero = personalAbm.agregarCocinero("Juan", "Perez", 31111222, LocalDate.of(1990, 5, 10),
					LocalDate.of(2024, 1, 15), 500000, "juan@mail.com", "Parrilla", 50000);
			System.out.printf("Cocinero creado, id=%d%n", idCocinero);

			Festival festival = festivalAbm.traer(idFestival);
			Personal responsable = personalAbm.traer(idCocinero);

			long idFoodTruck = unidadVentaAbm.agregarFoodTruck("El Fueguito", 15.5f, "FT00100001", festival,
					responsable, true, "AA123BB");
			System.out.printf("FoodTruck creado, id=%d%n", idFoodTruck);

			UnidadVenta foodTruck = unidadVentaAbm.traer(idFoodTruck);

			long idChoripan = platoAbm.agregar("Choripán", 3500, 1500, foodTruck);
			long idPapas = platoAbm.agregar("Papas Fritas", 2500, 800, foodTruck);
			System.out.printf("Platos creados, ids=%d,%d%n", idChoripan, idPapas);

			long idPedido = pedidoAbm.agregar(LocalDate.of(2026, 9, 1), festival, foodTruck);
			System.out.printf("Pedido creado, id=%d%n", idPedido);

			detallesAbm.agregar(3, platoAbm.traer(idChoripan), pedidoAbm.traer(idPedido));
			detallesAbm.agregar(2, platoAbm.traer(idPapas), pedidoAbm.traer(idPedido));
			System.out.println("Detalles de pedido cargados.");
		}

		// --- 2 FoodTrucks mas: La Empanada Andante y Sushi On Wheels ---
		{
			FestivalABM festivalAbm = new FestivalABM();
			PersonalABM personalAbm = new PersonalABM();
			UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
			PlatoABM platoAbm = new PlatoABM();

			Festival festival = festivalAbm.traer(1);
			Personal responsable = personalAbm.traer(1); // reutiliza el Cocinero ya cargado como responsable

			long idEmpanadas = unidadVentaAbm.agregarFoodTruck("La Empanada Andante", 12.0f, "FT00000002", festival,
					responsable, false, "AB456CD");
			System.out.printf("FoodTruck creado, id=%d%n", idEmpanadas);
			UnidadVenta empanadas = unidadVentaAbm.traer(idEmpanadas);
			platoAbm.agregar("Empanada de Carne", 800, 300, empanadas);
			platoAbm.agregar("Empanada de Pollo", 800, 300, empanadas);
			platoAbm.agregar("Empanada de Verdura", 750, 280, empanadas);

			long idSushi = unidadVentaAbm.agregarFoodTruck("Sushi On Wheels", 18.0f, "FT00000003", festival,
					responsable, true, "AC789EF");
			System.out.printf("FoodTruck creado, id=%d%n", idSushi);
			UnidadVenta sushi = unidadVentaAbm.traer(idSushi);
			platoAbm.agregar("Combo California x10", 6000, 2500, sushi);
			platoAbm.agregar("Combo Philadelphia x10", 6500, 2800, sushi);

			System.out.println("Platos de ambos FoodTrucks creados.");
		}

		// --- Staff (Cajero + Cocinero) asignado a El Fueguito ---
		{
			long idUnidadVenta = 1;

			UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
			PersonalABM personalAbm = new PersonalABM();

			UnidadVenta unidad = unidadVentaAbm.traer(idUnidadVenta);

			long idCajero = personalAbm.agregarCajero("Lucía", "Gómez", 32555111, LocalDate.of(1995, 3, 20),
					LocalDate.of(2025, 6, 1), 450000, "lucia@mail.com", "Noche", 20000, unidad);
			System.out.printf("Cajero creado y asignado a la unidad id=%d, idPersonal=%d%n", idUnidadVenta, idCajero);

			long idCocinero = personalAbm.agregarCocinero("Marcos", "Diaz", 33111222, LocalDate.of(1992, 7, 12),
					LocalDate.of(2025, 1, 10), 480000, "marcos@mail.com", "Parrilla", 40000, unidad);
			System.out.printf("Cocinero creado y asignado a la unidad id=%d, idPersonal=%d%n", idUnidadVenta,
					idCocinero);
		}

		// --- Mas staff para Empanada Andante y Sushi, y pedidos para todos los FoodTrucks ---
		{
			FestivalABM festivalAbm = new FestivalABM();
			PersonalABM personalAbm = new PersonalABM();
			UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
			PedidoABM pedidoAbm = new PedidoABM();
			DetallesPedidoABM detallesAbm = new DetallesPedidoABM();

			Festival festival = festivalAbm.traer(1);

			List<UnidadVenta> foodTrucks = unidadVentaAbm.traerFoodTrucksDeFestival(festival);
			UnidadVenta elFueguito = buscarPorCodigo(foodTrucks, "FT00100001");
			UnidadVenta empanadas = buscarPorCodigo(foodTrucks, "FT00000002");
			UnidadVenta sushi = buscarPorCodigo(foodTrucks, "FT00000003");

			personalAbm.agregarCajero("Rocío", "Fernandez", 34222111, LocalDate.of(1998, 4, 15),
					LocalDate.of(2026, 1, 5), 420000, "rocio@mail.com", "Mañana", 15000, empanadas);
			personalAbm.agregarCocinero("Diego", "Silva", 30987654, LocalDate.of(1988, 11, 2),
					LocalDate.of(2024, 8, 20), 500000, "diego@mail.com", "Panaderia", 35000, empanadas);
			personalAbm.agregarCajero("Valentina", "Ortiz", 36123456, LocalDate.of(2000, 9, 10),
					LocalDate.of(2026, 3, 1), 410000, "valentina@mail.com", "Noche", 18000, sushi);
			personalAbm.agregarCocinero("Kenji", "Tanaka", 28555444, LocalDate.of(1985, 1, 28),
					LocalDate.of(2023, 5, 15), 600000, "kenji@mail.com", "Sushi", 60000, sushi);
			System.out.println("Staff adicional cargado para La Empanada Andante y Sushi On Wheels.");

			long idPedido2 = pedidoAbm.agregar(LocalDate.of(2026, 9, 2), festival, elFueguito);
			var pedido2 = pedidoAbm.traer(idPedido2);
			for (Plato p : elFueguito.getPlatos()) {
				detallesAbm.agregar(5, p, pedido2);
			}
			System.out.printf("Segundo pedido creado para El Fueguito, id=%d%n", idPedido2);

			long idPedidoEmpanadas = pedidoAbm.agregar(LocalDate.of(2026, 9, 2), festival, empanadas);
			var pedidoEmpanadas = pedidoAbm.traer(idPedidoEmpanadas);
			for (Plato p : empanadas.getPlatos()) {
				detallesAbm.agregar(6, p, pedidoEmpanadas);
			}
			System.out.printf("Pedido creado para La Empanada Andante, id=%d%n", idPedidoEmpanadas);

			long idPedidoSushi = pedidoAbm.agregar(LocalDate.of(2026, 9, 3), festival, sushi);
			var pedidoSushi = pedidoAbm.traer(idPedidoSushi);
			for (Plato p : sushi.getPlatos()) {
				detallesAbm.agregar(3, p, pedidoSushi);
			}
			System.out.printf("Pedido creado para Sushi On Wheels, id=%d%n", idPedidoSushi);

			System.out.println("Datos adicionales cargados con exito.");
		}

		// --- 7 FoodTrucks mas (llegando a 10), con platos, pedidos, y 2 empleados mas ---
		{
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

			UnidadVenta waffleWorld = buscarPorCodigo(unidadVentaAbm.traerFoodTrucksDeFestival(festival),
					"FT00000004");
			UnidadVenta tacosLocos = buscarPorCodigo(unidadVentaAbm.traerFoodTrucksDeFestival(festival),
					"FT00000005");

			long idNahuel = personalAbm.agregarCajero("Nahuel", "Torres", 37444555, LocalDate.of(1997, 6, 18),
					LocalDate.of(2026, 2, 1), 415000, "nahuel@mail.com", "Mañana", 16000, waffleWorld);
			System.out.printf("Cajero creado y asignado a Waffle World, id=%d%n", idNahuel);

			long idCamila = personalAbm.agregarCocinero("Camila", "Rios", 35666777, LocalDate.of(1993, 2, 25),
					LocalDate.of(2025, 11, 1), 470000, "camila@mail.com", "Comida rapida", 30000, tacosLocos);
			System.out.printf("Cocinero creado y asignado a Tacos Locos, id=%d%n", idCamila);

			System.out.println("Carga masiva completada: 10 FoodTrucks y personal adicional listos.");
		}

		// --- Puesto Desarmable, con su responsable, platos y pedido ---
		{
			long idFestival = 1;

			FestivalABM festivalAbm = new FestivalABM();
			PersonalABM personalAbm = new PersonalABM();
			PlatoABM platoAbm = new PlatoABM();
			UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
			PedidoABM pedidoAbm = new PedidoABM();
			DetallesPedidoABM detallesAbm = new DetallesPedidoABM();

			long idCajero = personalAbm.agregarCajero("Pedro", "Gomez", 29224300, LocalDate.of(1987, 3, 17),
					LocalDate.of(2025, 2, 10), 400000, "PedroGomez@gmail.com", "Noche", 30000);
			System.out.printf("Cajero creado, id=%d%n", idCajero);

			Festival festival = festivalAbm.traer(idFestival);
			Personal responsable = personalAbm.traer(idCajero);

			long idPuestoDesarmable = unidadVentaAbm.agregarPuestoDesarmable("La carpa de Pedro", 8.5f, "PD00000401",
					festival, responsable, 2, 30);
			System.out.printf("Puesto Desarmable creado, id=%d%n", idPuestoDesarmable);

			UnidadVenta puesto = unidadVentaAbm.traer(idPuestoDesarmable);

			platoAbm.agregar("Milanesa", 4000, 2000, puesto);
			platoAbm.agregar("Ensalada rusa", 3000, 400, puesto);
			platoAbm.agregar("Ensalada de lechuga", 3000, 400, puesto);
			System.out.println("Platos del puesto creados.");

			long idPedido = pedidoAbm.agregar(LocalDate.of(2026, 9, 1), festival, puesto);
			System.out.printf("Pedido creado, id=%d%n", idPedido);

			long idChoripan = platoAbm.agregar("Choripán", 3000, 1500, puesto);
			long idPapas = platoAbm.agregar("Papas Fritas", 2000, 800, puesto);

			detallesAbm.agregar(3, platoAbm.traer(idChoripan), pedidoAbm.traer(idPedido));
			detallesAbm.agregar(2, platoAbm.traer(idPapas), pedidoAbm.traer(idPedido));
		}

		System.out.println("=== Toda la carga de datos de prueba se completo con exito ===");
	}

	private static UnidadVenta buscarPorCodigo(List<UnidadVenta> lista, String codigo) {
		return lista.stream().filter(u -> u.getCodigo().equals(codigo)).findFirst()
				.orElseThrow(() -> new IllegalStateException("No se encontro la unidad con codigo " + codigo));
	}
}
