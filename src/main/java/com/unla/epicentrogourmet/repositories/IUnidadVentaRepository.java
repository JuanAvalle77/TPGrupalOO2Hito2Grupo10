package com.unla.epicentrogourmet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.UnidadVenta;

@Repository
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {
	boolean existsByCodigo(String codigo);

	/** Unidades asociadas a un festival. */
	List<UnidadVenta> findByFestivalId(Long festivalId);

	/** Unidades de venta a cargo de un responsable. */
	List<UnidadVenta> findByResponsableId(Long responsableId);

	boolean existsByCodigo(String codigo);
}
