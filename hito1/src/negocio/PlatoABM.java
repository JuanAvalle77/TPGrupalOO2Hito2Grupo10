package negocio;

import java.util.List;

import dao.PlatoDao;
import datos.Plato;
import datos.UnidadVenta;

public class PlatoABM {

	PlatoDao dao = new PlatoDao();

	public int agregar(String nombre, double precioVenta, double costoProduccion, UnidadVenta unidadVenta) {
		Plato p = new Plato(nombre, precioVenta, costoProduccion, unidadVenta);
		return dao.agregar(p);
	}

	public Plato traer(long idPlato) {
		return dao.traer(idPlato);
	}

	public List<Plato> traer() {
		return dao.traer();
	}
}
