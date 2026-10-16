package test;

import java.util.List;

import datos.UnidadVenta;
import datos.PuestoDesarmable;
import negocio.FestivalABM;
import negocio.UnidadVentaABM;

public class TestPuestosDesarmablesSuperficie {

	public static void main(String[] args) {
		UnidadVentaABM abm = new UnidadVentaABM();
		FestivalABM Festivalabm = new FestivalABM();
		long idFestival = 1; 
		
		System.out.printf("%nSuperficie total: %f%n%n", abm.traerSuperficieTotalUnidadVenta(Festivalabm.traer(idFestival)));
		System.out.printf("%nSuperficie total de puestos desarmables: %f%n", abm.traerSuperficieTotalPuestosDesarmables(Festivalabm.traer(idFestival)));
	}

}
