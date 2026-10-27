package com.unla.epicentrogourmet.services.implementation;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.CanonUnidadDTO;
import com.unla.epicentrogourmet.dtos.RankingUnidadDTO;
import com.unla.epicentrogourmet.dtos.ReporteMayoresCanonDTO;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.Pedido;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.services.IAdminReportService;
import com.unla.epicentrogourmet.services.ICanonService;

@Service
public class AdminReportService implements IAdminReportService {

	private final IFestivalRepository festivalRepository;
	private final IUnidadVentaRepository unidadVentaRepository;
	private final IPedidoRepository pedidoRepository;
	private final ICanonService canonService;

	public AdminReportService(IFestivalRepository festivalRepository, IUnidadVentaRepository unidadVentaRepository,
			IPedidoRepository pedidoRepository, ICanonService canonService) {
		this.festivalRepository = festivalRepository;
		this.unidadVentaRepository = unidadVentaRepository;
		this.pedidoRepository = pedidoRepository;
		this.canonService = canonService;
	}

	@Override
	@Transactional(readOnly = true)
	public Festival obtenerFestival(Long festivalId) {
		return festivalRepository.findById(festivalId)
				.orElseThrow(() -> new IllegalArgumentException("No existe el festival seleccionado"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnidadVenta> unidadesDelFestival(Long festivalId) {
		obtenerFestival(festivalId);
		return unidadVentaRepository.findByFestivalId(festivalId).stream()
				.sorted(Comparator.comparing(UnidadVenta::getNombreComercial, String.CASE_INSENSITIVE_ORDER)).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RankingUnidadDTO> ranking(Long festivalId) {
		List<UnidadVenta> unidades = unidadesDelFestival(festivalId);
		Map<Long, Double> recaudacionPorUnidad = pedidoRepository.findByFestivalId(festivalId).stream()
				.collect(Collectors.groupingBy(p -> p.getUnidadVenta().getId(),
						Collectors.summingDouble(Pedido::calcularTotal)));
		return unidades.stream()
				.map(u -> new RankingUnidadDTO(u.getId(), u.getNombreComercial(), u.getCodigo(),
						recaudacionPorUnidad.getOrDefault(u.getId(), 0.0)))
				.sorted(Comparator.comparingDouble(RankingUnidadDTO::getRecaudacion).reversed()
						.thenComparing(RankingUnidadDTO::getNombre, String.CASE_INSENSITIVE_ORDER))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReporteMayoresCanonDTO> mayoresCanones(Long festivalId, int cantidad) {
		if (cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
		}
		return unidadesDelFestival(festivalId).stream()
				.map(canonService::calcularDetalle)
				.map(this::aReporte)
				.sorted(Comparator.comparingDouble(ReporteMayoresCanonDTO::getCanon).reversed()
						.thenComparing(ReporteMayoresCanonDTO::getNombreComercial, String.CASE_INSENSITIVE_ORDER))
				.limit(cantidad)
				.toList();
	}

	private ReporteMayoresCanonDTO aReporte(CanonUnidadDTO canon) {
		return new ReporteMayoresCanonDTO(canon.getNombreComercial(), canon.getCodigo(), canon.getTipo(), canon.getCanon());
	}
}
