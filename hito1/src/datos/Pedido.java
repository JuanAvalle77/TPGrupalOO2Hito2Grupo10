package datos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class Pedido {

	private long idPedido;
	private LocalDate fecha;
	private Festival festival;
	private UnidadVenta unidadVenta;
	private Set<DetallesPedido> detalles = new HashSet<>();

	public Pedido() {
	}

	public Pedido(LocalDate fecha, Festival festival, UnidadVenta unidadVenta) {
		super();
		this.fecha = fecha;
		this.festival = festival;
		this.unidadVenta = unidadVenta;
	}

	public double calcularTotal() {
		return detalles.stream().mapToDouble(d -> d.getPlato().getPrecioVenta() * d.getCantidad()).sum();
	}

	public long getIdPedido() {
		return idPedido;
	}

	protected void setIdPedido(long idPedido) {
		this.idPedido = idPedido;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Festival getFestival() {
		return festival;
	}

	public void setFestival(Festival festival) {
		this.festival = festival;
	}

	public UnidadVenta getUnidadVenta() {
		return unidadVenta;
	}

	public void setUnidadVenta(UnidadVenta unidadVenta) {
		this.unidadVenta = unidadVenta;
	}

	public Set<DetallesPedido> getDetalles() {
		return detalles;
	}

	public void setDetalles(Set<DetallesPedido> detalles) {
		this.detalles = detalles;
	}

	@Override
	public String toString() {
		return "Pedido [idPedido=" + idPedido + ", fecha=" + fecha + "]";
	}
}
