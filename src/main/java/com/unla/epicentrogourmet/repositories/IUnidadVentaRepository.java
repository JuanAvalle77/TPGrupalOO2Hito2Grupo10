package com.unla.epicentrogourmet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.UnidadVenta;

@Repository
public interface IUnidadVentaRepository extends JpaRepository<UnidadVenta, Long> {
}
