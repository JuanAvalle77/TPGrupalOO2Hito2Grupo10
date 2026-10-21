package com.unla.epicentrogourmet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.CierreCaja;
import java.time.LocalDate;

@Repository
public interface ICierreCajaRepository extends JpaRepository<CierreCaja, Long> {
	boolean existsByCajero_IdAndFecha(Long cajeroId, LocalDate fecha);
}
