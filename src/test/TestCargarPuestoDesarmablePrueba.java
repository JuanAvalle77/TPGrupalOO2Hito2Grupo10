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

public class TestCargarPuestoDesarmablePrueba {

	public static void main(String[] args) {

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
}
