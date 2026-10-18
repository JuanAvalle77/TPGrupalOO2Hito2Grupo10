package negocio;

import dao.DetallesPedidoDao;
import datos.DetallesPedido;
import datos.Pedido;
import datos.Plato;

public class DetallesPedidoABM {

	DetallesPedidoDao dao = new DetallesPedidoDao();

	public int agregar(int cantidad, Plato plato, Pedido pedido) {
		DetallesPedido d = new DetallesPedido(cantidad, plato, pedido);
		return dao.agregar(d);
	}
}
