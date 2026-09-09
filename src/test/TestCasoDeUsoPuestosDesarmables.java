package test;

import java.util.List;

import datos.PuestoDesarmable;
import datos.UnidadVenta;
import negocio.UnidadVentaABM;
import negocio.FestivalABM;

/**
 * Caso de Uso: Puestos Desarmables de un festival junto con la cantidad de
 * platos que ofrece cada uno y la superficie total ocupada.
 * Combina Herencia (PuestoDesarmable extiende UnidadVenta) y Uno a Muchos
 * (Festival -> UnidadVenta).
 */
public class TestCasoDeUsoPuestosDesarmables {

	public static void main(String[] args) {

		long idFestival = 1; // ajustar segun el id que haya quedado al correr TestCargarPuestoDesarmablePrueba

		UnidadVentaABM abm = new UnidadVentaABM();
		FestivalABM Festivalabm = new FestivalABM();

		System.out.printf("Primer caso de uso: Consultar lista de puestos desarmables");
		List<UnidadVenta> puestos = abm.traerPuestosDesarmablesDeFestival(Festivalabm.traer(1));
		System.out.printf("Puestos Desarmables del festival id=%d%n", idFestival);
		for (UnidadVenta u : puestos) {
			PuestoDesarmable pd = (PuestoDesarmable) u;
			System.out.printf("- %s (codigo=%s) -> %d platos, %d carpas, %d minutos de montaje, superficie %.1fm2%n",
					pd.getNombre(), pd.getCodigo(), pd.getPlatos().size(), pd.getCantidadCarpas(),
					pd.getTiempoMontaje(), pd.getSuperficie());
		}
	}
}
