package test;

import java.time.LocalDate;
import java.util.List;

import datos.Festival;
import datos.Plato;
import datos.UnidadVenta;
import negocio.DetallesPedidoABM;
import negocio.FestivalABM;
import negocio.PedidoABM;
import negocio.PersonalABM;
import negocio.UnidadVentaABM;

/**
 * Carga datos adicionales de prueba: mas staff (Personal) asignado a los
 * FoodTrucks, y pedidos con detalles para todos los FoodTrucks del festival
 * (asi la facturacion y el listado de staff muestran varios registros, no
 * solo uno).
 */
public class TestCargarMasDatosPrueba {

	public static void main(String[] args) {

		FestivalABM festivalAbm = new FestivalABM();
		PersonalABM personalAbm = new PersonalABM();
		UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
		PedidoABM pedidoAbm = new PedidoABM();
		DetallesPedidoABM detallesAbm = new DetallesPedidoABM();

		Festival festival = festivalAbm.traer(1);

		// Traigo los FoodTrucks con sus platos ya cargados (evita problemas de lazy loading)
		List<UnidadVenta> foodTrucks = unidadVentaAbm.traerFoodTrucksDeFestival(festival);
		UnidadVenta elFueguito = buscarPorCodigo(foodTrucks, "FT00100001");
		UnidadVenta empanadas = buscarPorCodigo(foodTrucks, "FT00000002");
		UnidadVenta sushi = buscarPorCodigo(foodTrucks, "FT00000003");

		// --- Mas staff ---
		personalAbm.agregarCajero("Rocío", "Fernandez", 34222111, LocalDate.of(1998, 4, 15),
				LocalDate.of(2026, 1, 5), 420000, "rocio@mail.com", "Mañana", 15000, empanadas);
		personalAbm.agregarCocinero("Diego", "Silva", 30987654, LocalDate.of(1988, 11, 2),
				LocalDate.of(2024, 8, 20), 500000, "diego@mail.com", "Panaderia", 35000, empanadas);
		personalAbm.agregarCajero("Valentina", "Ortiz", 36123456, LocalDate.of(2000, 9, 10),
				LocalDate.of(2026, 3, 1), 410000, "valentina@mail.com", "Noche", 18000, sushi);
		personalAbm.agregarCocinero("Kenji", "Tanaka", 28555444, LocalDate.of(1985, 1, 28),
				LocalDate.of(2023, 5, 15), 600000, "kenji@mail.com", "Sushi", 60000, sushi);
		System.out.println("Staff adicional cargado para La Empanada Andante y Sushi On Wheels.");

		// --- Segundo pedido para El Fueguito ---
		long idPedido2 = pedidoAbm.agregar(LocalDate.of(2026, 9, 2), festival, elFueguito);
		var pedido2 = pedidoAbm.traer(idPedido2);
		for (Plato p : elFueguito.getPlatos()) {
			detallesAbm.agregar(5, p, pedido2);
		}
		System.out.printf("Segundo pedido creado para El Fueguito, id=%d%n", idPedido2);

		// --- Pedido para La Empanada Andante ---
		long idPedidoEmpanadas = pedidoAbm.agregar(LocalDate.of(2026, 9, 2), festival, empanadas);
		var pedidoEmpanadas = pedidoAbm.traer(idPedidoEmpanadas);
		for (Plato p : empanadas.getPlatos()) {
			detallesAbm.agregar(6, p, pedidoEmpanadas);
		}
		System.out.printf("Pedido creado para La Empanada Andante, id=%d%n", idPedidoEmpanadas);

		// --- Pedido para Sushi On Wheels ---
		long idPedidoSushi = pedidoAbm.agregar(LocalDate.of(2026, 9, 3), festival, sushi);
		var pedidoSushi = pedidoAbm.traer(idPedidoSushi);
		for (Plato p : sushi.getPlatos()) {
			detallesAbm.agregar(3, p, pedidoSushi);
		}
		System.out.printf("Pedido creado para Sushi On Wheels, id=%d%n", idPedidoSushi);

		System.out.println("Datos adicionales cargados con exito.");
	}

	private static UnidadVenta buscarPorCodigo(List<UnidadVenta> lista, String codigo) {
		return lista.stream().filter(u -> u.getCodigo().equals(codigo)).findFirst()
				.orElseThrow(() -> new IllegalStateException("No se encontro la unidad con codigo " + codigo));
	}
}
