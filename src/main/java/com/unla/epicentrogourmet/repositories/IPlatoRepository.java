package com.unla.epicentrogourmet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Plato;
import java.util.List;

@Repository
public interface IPlatoRepository extends JpaRepository<Plato, Long> {
	List<Plato> findByUnidadVentaIdOrderByNombreAsc(Long unidadVentaId);
}
