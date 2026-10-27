package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.PlatoDTO;
import com.unla.epicentrogourmet.dtos.RentabilidadDTO;
import com.unla.epicentrogourmet.dtos.ReporteVentaDTO;
import com.unla.epicentrogourmet.entities.DetallesPedido;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Pedido;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.ICanonService;
import com.unla.epicentrogourmet.services.ILiquidacionService;
import com.unla.epicentrogourmet.services.IReportesResponsableService;

@Service
public class ReportesResponsableService implements IReportesResponsableService {

	private final IFestivalRepository festivalRepository;
	private final IPedidoRepository pedidoRepository;
	private final ICanonService canonService;
	private final ILiquidacionService liquidacionService;

	public ReportesResponsableService(IFestivalRepository festivalRepository, IPedidoRepository pedidoRepository,
			ICanonService canonService, ILiquidacionService liquidacionService) {
		this.festivalRepository = festivalRepository;
		this.pedidoRepository = pedidoRepository;
		this.canonService = canonService;
		this.liquidacionService = liquidacionService;
	}

	@Override
	@Transactional(readOnly = true)
	public ReporteVentaDTO reporteRecaudacion(Long festivalId) {
		Festival festival = festivalRepository.findById(festivalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe el festival seleccionado"));
		List<Pedido> pedidos = pedidoRepository.findByFestivalId(festivalId);
		double total = pedidos.stream().mapToDouble(Pedido::calcularTotal).sum();
		return new ReporteVentaDTO(festival.getNombre(), pedidos.size(), total);
	}

	@Override
	@Transactional(readOnly = true)
	public RentabilidadDTO rentabilidad(Long unidadId, LocalDate desde, LocalDate hasta,
			List<UnidadVenta> unidadesPermitidas) {
		UnidadVenta unidad = unidadesPermitidas.stream().filter(u -> u.getId().equals(unidadId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Esa unidad no está a tu cargo"));
		Festival festival = unidad.getFestival();
		LocalDate inicio = desde == null ? festival.getFechaInicio() : desde;
		LocalDate fin = hasta == null ? festival.getFechaFin() : hasta;
		if ((desde == null) != (hasta == null)) {
			throw new IllegalArgumentException("Indicá las dos fechas o dejá ambas vacías para consultar todo el festival");
		}
		if (fin.isBefore(inicio)) {
			throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
		}
		if (inicio.isBefore(festival.getFechaInicio()) || fin.isAfter(festival.getFechaFin())) {
			throw new IllegalArgumentException("El rango debe estar dentro del período del festival");
		}

		List<Pedido> pedidos = pedidoRepository.findByFestivalId(festival.getId()).stream()
				.filter(p -> p.getUnidadVenta().getId().equals(unidad.getId()))
				.filter(p -> !p.getFecha().isBefore(inicio) && !p.getFecha().isAfter(fin)).toList();
		double ventas = pedidos.stream().mapToDouble(Pedido::calcularTotal).sum();
		double costoPlatos = pedidos.stream().mapToDouble(Pedido::calcularCostoPlatos).sum();
		long diasRango = ChronoUnit.DAYS.between(inicio, fin) + 1;
		double proporcion = (double) diasRango / festival.getDuracionEnDias();
		double sueldos = liquidacionService.liquidarUnidad(unidad).getTotalUnidad() * proporcion;
		double canon = canonService.calcularCanon(unidad) * proporcion;
		return new RentabilidadDTO(unidad.getNombreComercial(), festival.getNombre(), inicio, fin, ventas,
				costoPlatos, sueldos, canon, ventas - costoPlatos - sueldos - canon);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PlatoDTO> platoEstrella(Long unidadId, List<UnidadVenta> unidadesPermitidas) {
		UnidadVenta unidad = unidadesPermitidas.stream().filter(u -> u.getId().equals(unidadId)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Esa unidad no está a tu cargo"));
		Map<Long, AcumuladoPlato> porPlato = new HashMap<>();
		pedidoRepository.findByFestivalId(unidad.getFestival().getId()).stream()
				.filter(p -> p.getUnidadVenta().getId().equals(unidadId))
				.forEach(pedido -> pedido.getDetalles().forEach(detalle -> acumular(porPlato, pedido, detalle)));
		return porPlato.values().stream()
				.sorted((a, b) -> {
					int porUnidades = Long.compare(b.unidadesVendidas, a.unidadesVendidas);
					if (porUnidades != 0) return porUnidades;
					int porPedidos = Long.compare(b.pedidos.size(), a.pedidos.size());
					return porPedidos != 0 ? porPedidos : a.nombre.compareToIgnoreCase(b.nombre);
				})
				.findFirst()
				.map(a -> new PlatoDTO(a.nombre, a.precioVenta, a.pedidos.size(), a.unidadesVendidas));
	}

	private void acumular(Map<Long, AcumuladoPlato> acumulados, Pedido pedido, DetallesPedido detalle) {
		Plato plato = detalle.getPlato();
		AcumuladoPlato acumulado = acumulados.computeIfAbsent(plato.getId(),
				id -> new AcumuladoPlato(plato.getNombre(), plato.getPrecioVenta()));
		acumulado.unidadesVendidas += detalle.getCantidad();
		acumulado.pedidos.add(pedido.getId());
	}

	private static class AcumuladoPlato {
		private final String nombre;
		private final double precioVenta;
		private long unidadesVendidas;
		private final Set<Long> pedidos = new HashSet<>();

		private AcumuladoPlato(String nombre, double precioVenta) {
			this.nombre = nombre;
			this.precioVenta = precioVenta;
		}
	}
}
