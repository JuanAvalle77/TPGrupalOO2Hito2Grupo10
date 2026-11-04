package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import com.unla.epicentrogourmet.dtos.RentabilidadDiaDTO;
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
		if ((desde == null) != (hasta == null)) {
			throw new IllegalArgumentException("Indicá las dos fechas o dejá ambas vacías para consultar los días transcurridos del festival");
		}
		LocalDate hoy = LocalDate.now();
		if (desde == null && hoy.isBefore(festival.getFechaInicio())) {
			throw new IllegalArgumentException("El festival todavía no empezó: no hay ventas ni costos para calcular");
		}
		// Sin fechas se evalua desde el inicio hasta hoy (o hasta el fin, si el festival ya termino):
		// asi las ventas y los costos corresponden siempre a los mismos dias.
		LocalDate inicio = desde == null ? festival.getFechaInicio() : desde;
		LocalDate fin = hasta == null ? (hoy.isBefore(festival.getFechaFin()) ? hoy : festival.getFechaFin()) : hasta;
		if (fin.isBefore(inicio)) {
			throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
		}
		if (inicio.isBefore(festival.getFechaInicio()) || fin.isAfter(festival.getFechaFin())) {
			throw new IllegalArgumentException("El rango debe estar dentro del período del festival");
		}
		if (inicio.isAfter(hoy)) {
			throw new IllegalArgumentException("Ese período todavía no ocurrió: elegí días que ya pasaron");
		}
		// Los dias que todavia no pasaron no se evaluan: no tienen ventas posibles y no corresponde
		// cargarles costos (si no, un rango que llega hasta el fin del festival daria siempre negativo).
		final boolean recortadoAHoy = fin.isAfter(hoy);
		final LocalDate finEvaluado = recortadoAHoy ? hoy : fin;

		List<Pedido> pedidos = pedidoRepository.findByFestivalId(festival.getId()).stream()
				.filter(p -> p.getUnidadVenta().getId().equals(unidad.getId()))
				.filter(p -> !p.getFecha().isBefore(inicio) && !p.getFecha().isAfter(finEvaluado)).toList();
		// Costos fijos proporcionales a los dias: sueldos y canon son del festival completo, asi
		// que a cada dia le toca (costo total / dias del festival). Se calcula si cada dia fue rentable.
		long diasFestival = festival.getDuracionEnDias();
		double sueldoDiario = liquidacionService.liquidarUnidad(unidad).getTotalUnidad() / diasFestival;
		double canonDiario = canonService.calcularCanon(unidad) / diasFestival;
		List<RentabilidadDiaDTO> dias = new ArrayList<>();
		for (LocalDate dia = inicio; !dia.isAfter(finEvaluado); dia = dia.plusDays(1)) {
			final LocalDate fecha = dia;
			List<Pedido> delDia = pedidos.stream().filter(p -> p.getFecha().equals(fecha)).toList();
			double ventasDia = delDia.stream().mapToDouble(Pedido::calcularTotal).sum();
			double platosDia = delDia.stream().mapToDouble(Pedido::calcularCostoPlatos).sum();
			dias.add(new RentabilidadDiaDTO(fecha, ventasDia, platosDia, sueldoDiario, canonDiario,
					ventasDia - platosDia - sueldoDiario - canonDiario));
		}
		double ventas = dias.stream().mapToDouble(RentabilidadDiaDTO::getVentas).sum();
		double costoPlatos = dias.stream().mapToDouble(RentabilidadDiaDTO::getCostoPlatos).sum();
		long diasRango = dias.size();
		double proporcion = (double) diasRango / diasFestival;
		double sueldos = sueldoDiario * diasRango;
		double canon = canonDiario * diasRango;
		return new RentabilidadDTO(unidad.getNombreComercial(), festival.getNombre(), inicio, finEvaluado, ventas,
				costoPlatos, sueldos, canon, ventas - costoPlatos - sueldos - canon, diasRango, diasFestival,
				proporcion, dias, recortadoAHoy);
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
