package com.unla.epicentrogourmet.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.unla.epicentrogourmet.dtos.LiquidacionUnidadDTO;
import com.unla.epicentrogourmet.dtos.RentabilidadDTO;
import com.unla.epicentrogourmet.entities.DetallesPedido;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTruck;
import com.unla.epicentrogourmet.entities.Pedido;
import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.repositories.IPedidoRepository;
import com.unla.epicentrogourmet.services.implementation.ReportesResponsableService;

/**
 * Rentabilidad neta: los sueldos y el canon son del festival completo y se imputan en
 * proporcion a los dias evaluados (dias del periodo / dias del festival).
 */
class RentabilidadServiceTest {

	/** Costos del festival completo (13 dias). */
	private static final double SUELDOS_FESTIVAL = 130_000;
	private static final double CANON_FESTIVAL = 6_500;
	private static final double DELTA = 0.001;

	private IPedidoRepository pedidoRepository;
	private ReportesResponsableService servicio;
	private UnidadVenta unidad;
	private Festival festival;
	private Plato plato;
	private final LocalDate hoy = LocalDate.now();

	@BeforeEach
	void setUp() {
		pedidoRepository = mock(IPedidoRepository.class);
		ICanonService canonService = mock(ICanonService.class);
		ILiquidacionService liquidacionService = mock(ILiquidacionService.class);
		servicio = new ReportesResponsableService(mock(IFestivalRepository.class), pedidoRepository, canonService,
				liquidacionService);

		festival = festival(hoy.minusDays(4), hoy.plusDays(8)); // 13 dias, en curso (5 transcurridos)
		unidad = new FoodTruck();
		unidad.setId(1L);
		unidad.setNombreComercial("El Fueguito");
		unidad.setFestival(festival);

		plato = new Plato();
		plato.setId(1L);
		plato.setNombre("Choripan");
		plato.setPrecioVenta(10_000);
		plato.setCostoProduccion(4_000);

		when(canonService.calcularCanon(any(UnidadVenta.class))).thenReturn(CANON_FESTIVAL);
		when(liquidacionService.liquidarUnidad(any(UnidadVenta.class))).thenReturn(
				new LiquidacionUnidadDTO("El Fueguito", "Festival", 13, 0.5, List.of(), SUELDOS_FESTIVAL));
	}

	private Festival festival(LocalDate inicio, LocalDate fin) {
		Festival f = new Festival();
		f.setId(1L);
		f.setNombre("Festival de prueba");
		f.setFechaInicio(inicio);
		f.setFechaFin(fin);
		return f;
	}

	/** Un pedido de 10 platos (ventas 100.000, costo 40.000) en la fecha indicada. */
	private Pedido pedido(LocalDate fecha) {
		Pedido p = new Pedido();
		p.setFecha(fecha);
		p.setFestival(festival);
		p.setUnidadVenta(unidad);
		DetallesPedido d = new DetallesPedido();
		d.setCantidad(10);
		d.setPlato(plato);
		d.setPedido(p);
		p.setDetalles(Set.of(d));
		return p;
	}

	private void conPedidos(Pedido... pedidos) {
		when(pedidoRepository.findByFestivalId(1L)).thenReturn(List.of(pedidos));
	}

	@Test
	void sinFechasEvaluaLosDiasTranscurridosYProrrateaLosCostos() {
		conPedidos(pedido(festival.getFechaInicio()), pedido(hoy.minusDays(1)));

		RentabilidadDTO r = servicio.rentabilidad(1L, null, null, List.of(unidad));

		assertEquals(festival.getFechaInicio(), r.getDesde());
		assertEquals(hoy, r.getHasta());
		assertEquals(5, r.getDiasConsiderados());
		assertEquals(13, r.getDiasFestival());
		assertEquals(5.0 / 13, r.getProporcion(), DELTA);
		assertEquals(200_000, r.getVentas(), DELTA);
		assertEquals(80_000, r.getCostoPlatos(), DELTA);
		assertEquals(SUELDOS_FESTIVAL * 5 / 13, r.getSueldos(), DELTA);
		assertEquals(CANON_FESTIVAL * 5 / 13, r.getCanon(), DELTA);
		assertEquals(200_000 - 80_000 - 50_000 - 2_500, r.getRentabilidadNeta(), DELTA);
		assertTrue(r.getRentabilidadNeta() > 0, "con ventas de varios dias la rentabilidad no tiene por que ser negativa");
	}

	@Test
	void sinFechasEnUnFestivalTerminadoSeImputanTodosLosCostos() {
		festival.setFechaInicio(hoy.minusDays(20));
		festival.setFechaFin(hoy.minusDays(8)); // 13 dias, ya termino
		conPedidos(pedido(hoy.minusDays(15)));

		RentabilidadDTO r = servicio.rentabilidad(1L, null, null, List.of(unidad));

		assertEquals(13, r.getDiasConsiderados());
		assertEquals(1.0, r.getProporcion(), DELTA);
		assertEquals(SUELDOS_FESTIVAL, r.getSueldos(), DELTA);
		assertEquals(CANON_FESTIVAL, r.getCanon(), DELTA);
	}

	@Test
	void conRangoSoloCuentanLosPedidosDelRangoYSusDias() {
		LocalDate inicio = festival.getFechaInicio();
		conPedidos(pedido(inicio), pedido(inicio.plusDays(2)), pedido(inicio.plusDays(3)));

		RentabilidadDTO r = servicio.rentabilidad(1L, inicio, inicio.plusDays(2), List.of(unidad));

		assertEquals(3, r.getDiasConsiderados());
		assertEquals(200_000, r.getVentas(), DELTA); // el pedido del dia 4 queda afuera
		assertEquals(SUELDOS_FESTIVAL * 3 / 13, r.getSueldos(), DELTA);
		assertEquals(CANON_FESTIVAL * 3 / 13, r.getCanon(), DELTA);
	}

	@Test
	void dosRangosConsecutivosSumanElCostoDelFestivalCompleto() {
		LocalDate inicio = festival.getFechaInicio();
		conPedidos();

		RentabilidadDTO primero = servicio.rentabilidad(1L, inicio, inicio.plusDays(5), List.of(unidad));
		RentabilidadDTO segundo = servicio.rentabilidad(1L, inicio.plusDays(6), festival.getFechaFin(), List.of(unidad));

		assertEquals(SUELDOS_FESTIVAL, primero.getSueldos() + segundo.getSueldos(), DELTA);
		assertEquals(CANON_FESTIVAL, primero.getCanon() + segundo.getCanon(), DELTA);
	}

	@Test
	void seCalculaSiCadaDiaFueRentable() {
		LocalDate inicio = festival.getFechaInicio();
		// dia 1: ventas 100.000 y platos 40.000. Dia 2: sin ventas. Dia 3: ventas 200.000 y platos 80.000
		conPedidos(pedido(inicio), pedido(inicio.plusDays(2)), pedido(inicio.plusDays(2)));
		double costoDiario = (SUELDOS_FESTIVAL + CANON_FESTIVAL) / 13; // 10.500

		RentabilidadDTO r = servicio.rentabilidad(1L, inicio, inicio.plusDays(2), List.of(unidad));

		assertEquals(3, r.getDias().size());
		assertEquals(inicio, r.getDias().get(0).getFecha());
		assertEquals(100_000 - 40_000 - costoDiario, r.getDias().get(0).getResultado(), DELTA);
		assertTrue(r.getDias().get(0).isRentable());
		assertEquals(-costoDiario, r.getDias().get(1).getResultado(), DELTA); // sin ventas igual pesan los costos
		assertTrue(!r.getDias().get(1).isRentable());
		assertEquals(200_000 - 80_000 - costoDiario, r.getDias().get(2).getResultado(), DELTA);
		assertTrue(r.getDias().get(2).isRentable());
		assertEquals(2, r.getDiasRentables());
	}

	@Test
	void laSumaDeLosResultadosDiariosEsLaRentabilidadNeta() {
		LocalDate inicio = festival.getFechaInicio();
		conPedidos(pedido(inicio), pedido(inicio.plusDays(3)), pedido(hoy));

		RentabilidadDTO r = servicio.rentabilidad(1L, null, null, List.of(unidad));

		double suma = r.getDias().stream().mapToDouble(d -> d.getResultado()).sum();
		assertEquals(r.getDiasConsiderados(), r.getDias().size());
		assertEquals(r.getRentabilidadNeta(), suma, DELTA);
	}

	@Test
	void unFestivalQueTodaviaNoEmpezoNoSeCalcula() {
		festival.setFechaInicio(hoy.plusDays(10));
		festival.setFechaFin(hoy.plusDays(22));

		assertThrows(IllegalArgumentException.class, () -> servicio.rentabilidad(1L, null, null, List.of(unidad)));
	}

	@Test
	void unaSolaFechaSeRechaza() {
		assertThrows(IllegalArgumentException.class,
				() -> servicio.rentabilidad(1L, festival.getFechaInicio(), null, List.of(unidad)));
		assertThrows(IllegalArgumentException.class,
				() -> servicio.rentabilidad(1L, null, festival.getFechaFin(), List.of(unidad)));
	}

	@Test
	void unRangoInvertidoOFueraDelFestivalSeRechaza() {
		LocalDate inicio = festival.getFechaInicio();

		assertThrows(IllegalArgumentException.class,
				() -> servicio.rentabilidad(1L, inicio.plusDays(3), inicio, List.of(unidad)));
		assertThrows(IllegalArgumentException.class,
				() -> servicio.rentabilidad(1L, inicio.minusDays(1), inicio.plusDays(2), List.of(unidad)));
		assertThrows(IllegalArgumentException.class,
				() -> servicio.rentabilidad(1L, inicio, festival.getFechaFin().plusDays(1), List.of(unidad)));
	}

	@Test
	void unaUnidadQueNoEsDelResponsableSeRechaza() {
		assertThrows(IllegalArgumentException.class, () -> servicio.rentabilidad(99L, null, null, List.of(unidad)));
	}
}
