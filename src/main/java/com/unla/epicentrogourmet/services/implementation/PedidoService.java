package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.entities.DetallesPedido;
import com.unla.epicentrogourmet.entities.Pedido;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.IPedidoService;

/** Creación de pedidos con controles para las ventas del día en curso. */
@Service
public class PedidoService implements IPedidoService {
	private final IPedidoRepository pedidoRepository;

	public PedidoService(IPedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@Override
	@Transactional
	public Pedido crear(Pedido pedido) {
		if (pedido == null) {
			throw new IllegalArgumentException("El pedido es obligatorio");
		}
		if (pedido.getFecha() == null || !pedido.getFecha().equals(LocalDate.now())) {
			throw new IllegalArgumentException("Los pedidos solo se pueden cargar con la fecha de hoy");
		}
		if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
			throw new IllegalArgumentException("El pedido debe incluir al menos un producto");
		}
		for (DetallesPedido detalle : pedido.getDetalles()) {
			if (detalle == null || detalle.getCantidad() <= 0) {
				throw new IllegalArgumentException("La cantidad de cada producto debe ser mayor a cero");
			}
			detalle.setPedido(pedido);
		}
		if (pedido.getUnidadVenta() == null || pedido.getFestival() == null) {
			throw new IllegalArgumentException("El pedido debe tener una unidad y un festival asociados");
		}
		return pedidoRepository.save(pedido);
	}
}
