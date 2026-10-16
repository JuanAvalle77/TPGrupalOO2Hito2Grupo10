package com.unla.epicentrogourmet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.CierreCaja;

@Repository
public interface ICierreCajaRepository extends JpaRepository<CierreCaja, Long> {
}
