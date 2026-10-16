package negocio;

import java.time.LocalDate;
import java.util.List;

import dao.PedidoDao;
import datos.Festival;
import datos.Pedido;
import datos.UnidadVenta;

public class PedidoABM {

	PedidoDao dao = new PedidoDao();

	public int agregar(LocalDate fecha, Festival festival, UnidadVenta unidadVenta) {
		Pedido p = new Pedido(fecha, festival, unidadVenta);
		return dao.agregar(p);
	}

	public Pedido traer(long idPedido) {
		return dao.traer(idPedido);
	}

	public List<Pedido> traer() {
		return dao.traer();
	}
}
