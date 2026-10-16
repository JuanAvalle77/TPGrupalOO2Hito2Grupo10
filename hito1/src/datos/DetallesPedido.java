package datos;

public class DetallesPedido {

	private long idDetallesPedido;
	private int cantidad;
	private Plato plato;
	private Pedido pedido;

	public DetallesPedido() {
	}

	public DetallesPedido(int cantidad, Plato plato, Pedido pedido) {
		super();
		this.cantidad = cantidad;
		this.plato = plato;
		this.pedido = pedido;
	}

	public long getIdDetallesPedido() {
		return idDetallesPedido;
	}

	protected void setIdDetallesPedido(long idDetallesPedido) {
		this.idDetallesPedido = idDetallesPedido;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public Plato getPlato() {
		return plato;
	}

	public void setPlato(Plato plato) {
		this.plato = plato;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	@Override
	public String toString() {
		return "DetallesPedido [idDetallesPedido=" + idDetallesPedido + ", cantidad=" + cantidad + "]";
	}
}
