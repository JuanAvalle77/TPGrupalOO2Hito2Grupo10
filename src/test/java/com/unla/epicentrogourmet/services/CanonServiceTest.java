package com.unla.epicentrogourmet.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.dtos.CanonUnidadDTO;
import com.unla.epicentrogourmet.entities.CostoFestival;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTruck;
import com.unla.epicentrogourmet.entities.PuestoDesarmable;
import com.unla.epicentrogourmet.services.implementation.CanonService;

/** Valores de los datos de prueba (sql/datos_prueba.sql). */
class CanonServiceTest {

	private CanonService canonService;
	private Festival festival;

	@BeforeEach
	void setUp() {
		canonService = new CanonService();
		CostoFestival costos = new CostoFestival();
		costos.setCostoPorM2(500);
		costos.setPlusConexionElectrica(2000);
		costos.setCostoPorMinutoMontaje(10);
		festival = new Festival();
		festival.setNombre("Festival de prueba");
		festival.setCostos(costos);
	}

	private FoodTruck foodTruck(double superficie, boolean electricidad) {
		FoodTruck ft = new FoodTruck();
		ft.setNombreComercial("Food Truck");
		ft.setCodigo("FT00000001");
		ft.setSuperficie(superficie);
		ft.setRequiereElectricidad(electricidad);
		ft.setFestival(festival);
		return ft;
	}

	@Test
	void foodTruckConElectricidadSumaElPlus() {
		assertEquals(9750, canonService.calcularCanon(foodTruck(15.5, true)), 0.001);
	}

	@Test
	void foodTruckSinElectricidadNoSumaElPlus() {
		assertEquals(6000, canonService.calcularCanon(foodTruck(12, false)), 0.001);
	}

	@Test
	void puestoDescuentaElTiempoDeMontaje() {
		PuestoDesarmable puesto = new PuestoDesarmable();
		puesto.setNombreComercial("Puesto");
		puesto.setCodigo("PD00000001");
		puesto.setSuperficie(8.5);
		puesto.setTiempoMontaje(30);
		puesto.setFestival(festival);
		assertEquals(3950, canonService.calcularCanon(puesto), 0.001);
	}

	@Test
	void elDetalleIncluyeTipoYCalculo() {
		CanonUnidadDTO dto = canonService.calcularDetalle(foodTruck(15.5, true));
		assertEquals("Food Truck", dto.getTipo());
		assertEquals(9750, dto.getCanon(), 0.001);
		assertTrue(dto.getDetalleCalculo().contains("conexión eléctrica"));
	}

	@Test
	void sinCostosCargadosFallaConMensajeClaro() {
		festival.setCostos(null);
		IllegalStateException e = assertThrows(IllegalStateException.class,
				() -> canonService.calcularCanon(foodTruck(10, false)));
		assertTrue(e.getMessage().contains("no tiene costos"));
	}
}
