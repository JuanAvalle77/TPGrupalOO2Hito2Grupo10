package test;

import java.time.LocalDate;

import datos.UnidadVenta;
import negocio.PersonalABM;
import negocio.UnidadVentaABM;

/**
 * Carga un Cajero y un Cocinero asignados como staff (personal) de la unidad
 * id=1, para poder probar el caso de uso de Santiago (traerUnidadVentaConStaff).
 */
public class TestCargarStaffPrueba {

	public static void main(String[] args) {

		long idUnidadVenta = 1;

		UnidadVentaABM unidadVentaAbm = new UnidadVentaABM();
		PersonalABM personalAbm = new PersonalABM();

		UnidadVenta unidad = unidadVentaAbm.traer(idUnidadVenta);

		long idCajero = personalAbm.agregarCajero("Lucía", "Gómez", 32555111, LocalDate.of(1995, 3, 20),
				LocalDate.of(2025, 6, 1), 450000, "lucia@mail.com", "Noche", 20000, unidad);
		System.out.printf("Cajero creado y asignado a la unidad id=%d, idPersonal=%d%n", idUnidadVenta, idCajero);

		long idCocinero = personalAbm.agregarCocinero("Marcos", "Diaz", 33111222, LocalDate.of(1992, 7, 12),
				LocalDate.of(2025, 1, 10), 480000, "marcos@mail.com", "Parrilla", 40000, unidad);
		System.out.printf("Cocinero creado y asignado a la unidad id=%d, idPersonal=%d%n", idUnidadVenta, idCocinero);
	}
}
