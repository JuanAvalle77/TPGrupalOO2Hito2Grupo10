package com.unla.epicentrogourmet.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.dtos.LiquidacionDTO;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.CostoFestival;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.services.implementation.LiquidacionService;

/** Ejemplos de la consigna del Hito 2 y valores de los datos de prueba. */
class LiquidacionServiceTest {

	private LiquidacionService liquidacionService;
	private CostoFestival costos;

	@BeforeEach
	void setUp() {
		liquidacionService = new LiquidacionService();
		costos = new CostoFestival();
		costos.setSueldoBase(100000);
		costos.setPlusAntiguedadAnual(5000);
		costos.setPlusCocinero(150000);
		costos.setPlusCocineroAyudante(100000);
		costos.setPlusLavaplatos(50000);
	}

	/** Festival que dura exactamente "dias" dias y termina en la fecha dada. */
	private Festival festival(long dias, LocalDate fin) {
		Festival f = new Festival();
		f.setNombre("Festival de prueba");
		f.setFechaFin(fin);
		f.setFechaInicio(fin.minusDays(dias - 1));
		f.setCostos(costos);
		return f;
	}

	private Cocinero cocinero(RolCocina rol) {
		Cocinero c = new Cocinero();
		c.setNombre("Nombre");
		c.setApellido("Apellido");
		c.setRolCocina(rol);
		c.setFechaIngreso(LocalDate.of(2020, 1, 1));
		return c;
	}

	private Cajero cajero(LocalDate ingreso) {
		Cajero c = new Cajero();
		c.setNombre("Nombre");
		c.setApellido("Apellido");
		c.setFechaIngreso(ingreso);
		return c;
	}

	@Test
	void factorSegunLosTramosDeLaConsigna() {
		assertEquals(0.5, liquidacionService.calcularFactor(1), 0.0001);
		assertEquals(0.5, liquidacionService.calcularFactor(15), 0.0001);
		assertEquals(1.0, liquidacionService.calcularFactor(16), 0.0001);
		assertEquals(1.0, liquidacionService.calcularFactor(30), 0.0001);
		assertEquals(1.5, liquidacionService.calcularFactor(31), 0.0001);
		assertEquals(1.5, liquidacionService.calcularFactor(38), 0.0001);
		assertEquals(2.0, liquidacionService.calcularFactor(52), 0.0001);
	}

	@Test
	void ejemploDeLaConsignaLavaplatosEnFestivalDe38Dias() {
		Festival f = festival(38, LocalDate.of(2026, 7, 25));
		assertEquals(225000, liquidacionService.calcularSueldo(cocinero(RolCocina.LAVAPLATOS), f), 0.001);
	}

	@Test
	void ejemploDeLaConsignaCajeroConTresAniosEnFestivalDe52Dias() {
		Festival f = festival(52, LocalDate.of(2026, 2, 21));
		Cajero cajero = cajero(LocalDate.of(2023, 2, 21));
		assertEquals(230000, liquidacionService.calcularSueldo(cajero, f), 0.001);
	}

	@Test
	void cajeraDeLosDatosDePruebaConDosAniosDeAntiguedad() {
		Festival f = festival(38, LocalDate.of(2026, 7, 25));
		Cajero ana = cajero(LocalDate.of(2024, 2, 1));
		assertEquals(165000, liquidacionService.calcularSueldo(ana, f), 0.001);
	}

	@Test
	void festivalCortoPagaLaMitad() {
		Festival f = festival(13, LocalDate.of(2026, 10, 25));
		assertEquals(125000, liquidacionService.calcularSueldo(cocinero(RolCocina.COCINERO), f), 0.001);
		assertEquals(100000, liquidacionService.calcularSueldo(cocinero(RolCocina.COCINERO_AYUDANTE), f), 0.001);
	}

	@Test
	void laAntiguedadSeMideHastaElFinDelFestival() {
		Festival f = festival(10, LocalDate.of(2026, 5, 31));
		Cajero nuevo = cajero(LocalDate.of(2025, 6, 1)); // todavia no cumple el anio
		LiquidacionDTO dto = liquidacionService.calcularLiquidacion(nuevo, f);
		assertEquals(0, dto.getPlus(), 0.001);
		assertEquals(50000, dto.getTotal(), 0.001);
	}

	@Test
	void elDetalleTrae_RolConceptoYFactor() {
		Festival f = festival(38, LocalDate.of(2026, 7, 25));
		LiquidacionDTO dto = liquidacionService.calcularLiquidacion(cocinero(RolCocina.COCINERO), f);
		assertEquals("Cocinero", dto.getRol());
		assertEquals("Plus por categoría", dto.getConceptoPlus());
		assertEquals(250000, dto.getSubtotal(), 0.001);
		assertEquals(1.5, dto.getFactor(), 0.0001);
		assertEquals(375000, dto.getTotal(), 0.001);
	}

	@Test
	void sinCostosCargadosFallaConMensajeClaro() {
		Festival f = festival(10, LocalDate.of(2026, 5, 31));
		f.setCostos(null);
		assertThrows(IllegalStateException.class,
				() -> liquidacionService.calcularSueldo(cocinero(RolCocina.COCINERO), f));
	}
}
