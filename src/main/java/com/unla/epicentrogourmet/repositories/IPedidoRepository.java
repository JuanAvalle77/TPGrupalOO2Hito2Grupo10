package com.unla.epicentrogourmet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Pedido;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPedidoRepository extends JpaRepository<Pedido, Long> {
	List<Pedido> findByUnidadVenta_IdAndFecha(Long unidadVentaId, LocalDate fecha);
	List<Pedido> findByFestivalId(Long festivalId);
}
