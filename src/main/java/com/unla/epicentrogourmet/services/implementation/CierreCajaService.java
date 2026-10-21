package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.CierreCajaEstadoDTO;
import com.unla.epicentrogourmet.dtos.UnidadCajaDTO;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.CierreCaja;
import com.unla.epicentrogourmet.entities.Pedido;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.ICierreCajaRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.services.ICierreCajaService;

/** Recaudación diaria y cierre único por cajero y fecha. */
@Service
public class CierreCajaService implements ICierreCajaService {
	private final ICierreCajaRepository cierreCajaRepository;
	private final IPedidoRepository pedidoRepository;
	private final IUnidadVentaRepository unidadVentaRepository;

	public CierreCajaService(ICierreCajaRepository cierreCajaRepository, IPedidoRepository pedidoRepository,
			IUnidadVentaRepository unidadVentaRepository) {
		this.cierreCajaRepository = cierreCajaRepository;
		this.pedidoRepository = pedidoRepository;
		this.unidadVentaRepository = unidadVentaRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public CierreCajaEstadoDTO estadoDeHoy(Cajero cajero) {
		LocalDate hoy = LocalDate.now();
		List<UnidadCajaDTO> unidades = cajero.getUnidades().stream()
				.filter(unidad -> unidad.getFestival() != null && unidad.getFestival().estaActivo(hoy))
				.map(unidad -> new UnidadCajaDTO(unidad.getId(), unidad.getNombreComercial(), unidad.getFestival().getNombre(),
						totalDelDia(unidad.getId(), hoy)))
				.toList();
		return new CierreCajaEstadoDTO(hoy, cierreCajaRepository.existsByCajero_IdAndFecha(cajero.getId(), hoy), unidades);
	}

	@Override
	@Transactional
	public CierreCaja cerrar(Cajero cajero, Long unidadId) {
		LocalDate hoy = LocalDate.now();
		if (cierreCajaRepository.existsByCajero_IdAndFecha(cajero.getId(), hoy)) {
			throw new IllegalArgumentException("Ya cerraste la caja de hoy");
		}
		UnidadVenta unidad = unidadVentaRepository.findById(unidadId)
				.orElseThrow(() -> new IllegalArgumentException("La unidad seleccionada no existe"));
		boolean asignada = cajero.getUnidades().stream().anyMatch(u -> u.getId().equals(unidad.getId()));
		if (!asignada || unidad.getFestival() == null || !unidad.getFestival().estaActivo(hoy)) {
			throw new IllegalArgumentException("Solo podés cerrar la caja de una unidad tuya con festival activo hoy");
		}
		CierreCaja cierre = new CierreCaja();
		cierre.setFecha(hoy);
		cierre.setCajero(cajero);
		cierre.setUnidadVenta(unidad);
		cierre.setTotalRecaudado(totalDelDia(unidad.getId(), hoy));
		try {
			return cierreCajaRepository.saveAndFlush(cierre);
		} catch (DataIntegrityViolationException ex) {
			throw new IllegalArgumentException("Ya cerraste la caja de hoy", ex);
		}
	}

	private double totalDelDia(Long unidadId, LocalDate fecha) {
		return pedidoRepository.findByUnidadVenta_IdAndFecha(unidadId, fecha).stream()
				.mapToDouble(Pedido::calcularTotal).sum();
	}
}
