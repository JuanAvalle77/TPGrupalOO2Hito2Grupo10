package com.unla.epicentrogourmet.services;

import com.unla.epicentrogourmet.dtos.CierreCajaEstadoDTO;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.CierreCaja;

public interface ICierreCajaService {
	CierreCajaEstadoDTO estadoDeHoy(Cajero cajero);

	CierreCaja cerrar(Cajero cajero, Long unidadId);
}
