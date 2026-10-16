package datos;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class UnidadVenta {

	private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Z]{2}[0-9]{8}$");

	protected long idUnidadVenta;
	protected String nombre;
	protected float superficie;
	protected String codigo;
	protected Festival festival;
	protected Personal responsable;
	protected Set<Plato> platos = new HashSet<>();
	protected Set<Pedido> pedidosRealizados = new HashSet<>();
	protected Set<Personal> personal = new HashSet<>();

	public UnidadVenta() {
	}

	public UnidadVenta(String nombre, float superficie, String codigo, Festival festival, Personal responsable) {
		super();
		this.nombre = nombre;
		this.superficie = superficie;
		setCodigo(codigo);
		this.festival = festival;
		this.responsable = responsable;
	}

	public abstract double calcularCostoTotal();

	public long getIdUnidadVenta() {
		return idUnidadVenta;
	}

	protected void setIdUnidadVenta(long idUnidadVenta) {
		this.idUnidadVenta = idUnidadVenta;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public float getSuperficie() {
		return superficie;
	}

	public void setSuperficie(float superficie) {
		this.superficie = superficie;
	}

	public String getCodigo() {
		return codigo;
	}

	/**
	 * Codigo unico de 10 caracteres: 2 letras mayusculas + 8 digitos (ej:
	 * "FT00000001"). Logica de validacion propia pedida por la consigna.
	 */
	public void setCodigo(String codigo) {
		if (codigo == null || !PATRON_CODIGO.matcher(codigo).matches()) {
			throw new IllegalArgumentException(
					"Codigo invalido: debe tener 10 caracteres (2 letras mayusculas + 8 digitos). Recibido: " + codigo);
		}
		this.codigo = codigo;
	}

	public Festival getFestival() {
		return festival;
	}

	public void setFestival(Festival festival) {
		this.festival = festival;
	}

	public Personal getResponsable() {
		return responsable;
	}

	public void setResponsable(Personal responsable) {
		this.responsable = responsable;
	}

	public Set<Plato> getPlatos() {
		return platos;
	}

	public void setPlatos(Set<Plato> platos) {
		this.platos = platos;
	}

	public Set<Pedido> getPedidosRealizados() {
		return pedidosRealizados;
	}

	public void setPedidosRealizados(Set<Pedido> pedidosRealizados) {
		this.pedidosRealizados = pedidosRealizados;
	}

	public Set<Personal> getPersonal() {
		return personal;
	}

	public void setPersonal(Set<Personal> personal) {
		this.personal = personal;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [idUnidadVenta=" + idUnidadVenta + ", nombre=" + nombre + ", codigo="
				+ codigo + "]";
	}
}
