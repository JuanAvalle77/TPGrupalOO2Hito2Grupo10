package com.unla.epicentrogourmet.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.unla.epicentrogourmet.dtos.EmpleadoForm;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.FoodTruck;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.enums.Rol;
import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.enums.Turno;
import com.unla.epicentrogourmet.exceptions.ReglaDeNegocioException;
import com.unla.epicentrogourmet.repositories.IPersonalRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;
import com.unla.epicentrogourmet.services.implementation.EmpleadoService;
import com.unla.epicentrogourmet.services.implementation.MailService;

class EmpleadoServiceTest {

	private IPersonalRepository personalRepository;
	private IUsuarioRepository usuarioRepository;
	private IUnidadVentaRepository unidadVentaRepository;
	private MailService mailService;
	private PasswordEncoder passwordEncoder;
	private EmpleadoService servicio;

	private UnidadVenta unidad;
	private List<UnidadVenta> misUnidades;

	@BeforeEach
	void setUp() {
		personalRepository = mock(IPersonalRepository.class);
		usuarioRepository = mock(IUsuarioRepository.class);
		unidadVentaRepository = mock(IUnidadVentaRepository.class);
		mailService = mock(MailService.class);
		passwordEncoder = new BCryptPasswordEncoder();
		servicio = new EmpleadoService(personalRepository, usuarioRepository, unidadVentaRepository,
				passwordEncoder, mailService);

		unidad = new FoodTruck();
		unidad.setId(1L);
		unidad.setNombreComercial("El Fueguito");
		misUnidades = List.of(unidad);

		when(personalRepository.save(any(Personal.class))).thenAnswer(inv -> {
			Personal p = inv.getArgument(0);
			if (p.getId() == null) {
				p.setId(50L);
			}
			return p;
		});
	}

	private EmpleadoForm formCajero() {
		EmpleadoForm f = new EmpleadoForm();
		f.setNombre("Sofia");
		f.setApellido("Martinez");
		f.setDni(40123456);
		f.setFechaNacimiento(LocalDate.now().minusYears(25));
		f.setFechaIngreso(LocalDate.now().minusYears(1));
		f.setEmail("Sofia.Martinez@epicentro.com");
		f.setContacto("1155559999");
		f.setTipo(TipoEmpleado.CAJERO);
		f.setTurno(Turno.NOCHE);
		return f;
	}

	private Cajero cajeroExistente(boolean activo) {
		Cajero c = new Cajero();
		c.setId(11L);
		c.setNombre("Nahuel");
		c.setApellido("Lopez");
		c.setDni(38111000);
		c.setFechaNacimiento(LocalDate.of(1999, 12, 1));
		c.setFechaIngreso(LocalDate.of(2026, 1, 15));
		c.setEmail("nahuel.lopez@epicentro.com");
		c.setActivo(activo);
		c.setTurno(Turno.NOCHE);
		return c;
	}

	private void empleadoEnMisUnidades(Personal p) {
		when(personalRepository.findEmpleadoDeUnidades(eq(p.getId()), anyCollection())).thenReturn(Optional.of(p));
	}

	// ------------------------------------------------------------------ alta

	@Test
	void altaCreaElEmpleadoLoAsignaALaUnidadYLeCreaUnUsuarioEmpleado() {
		Personal alta = servicio.alta(formCajero(), misUnidades);

		assertTrue(alta instanceof Cajero);
		assertEquals(Turno.NOCHE, ((Cajero) alta).getTurno());
		assertTrue(alta.isActivo());
		assertEquals("sofia.martinez@epicentro.com", alta.getEmail());
		assertTrue(unidad.getPersonal().contains(alta));
		verify(unidadVentaRepository).save(unidad);

		ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepository).save(captor.capture());
		Usuario usuario = captor.getValue();
		assertEquals("sofia.martinez@epicentro.com", usuario.getUsername());
		assertEquals(Rol.EMPLEADO, usuario.getRol());
		assertTrue(usuario.isEnabled());
		assertEquals(alta, usuario.getPersonal());
	}

	@Test
	void altaDeCocinaCreaUnCocineroConSuRol() {
		EmpleadoForm f = formCajero();
		f.setTipo(TipoEmpleado.COCINA);
		f.setTurno(null);
		f.setRolCocina(RolCocina.LAVAPLATOS);

		Personal alta = servicio.alta(f, misUnidades);

		assertTrue(alta instanceof Cocinero);
		assertEquals(RolCocina.LAVAPLATOS, ((Cocinero) alta).getRolCocina());
	}

	@Test
	void laContraseniaSeEnviaPorMailYSeGuardaEncriptada() {
		servicio.alta(formCajero(), misUnidades);

		ArgumentCaptor<String> texto = ArgumentCaptor.forClass(String.class);
		verify(mailService).enviar(eq("sofia.martinez@epicentro.com"), anyString(), texto.capture());
		String cuerpo = texto.getValue();
		assertTrue(cuerpo.contains("sofia.martinez@epicentro.com"));

		String contrasenia = cuerpo.lines().filter(l -> l.contains("Contraseña:")).findFirst().orElseThrow()
				.split(":")[1].trim();
		assertEquals(10, contrasenia.length());

		ArgumentCaptor<Usuario> usuario = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepository).save(usuario.capture());
		assertNotEquals(contrasenia, usuario.getValue().getPassword());
		assertTrue(passwordEncoder.matches(contrasenia, usuario.getValue().getPassword()));
	}

	@Test
	void altaDeMenorDeEdadSeRechaza() {
		EmpleadoForm f = formCajero();
		f.setFechaNacimiento(LocalDate.now().minusYears(17));
		f.setFechaIngreso(LocalDate.now().minusDays(1));

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class, () -> servicio.alta(f, misUnidades));

		assertEquals("fechaNacimiento", e.getCampo());
		verify(mailService, never()).enviar(anyString(), anyString(), anyString());
		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void altaConDniDeUnEmpleadoActivoSeRechaza() {
		when(personalRepository.findByDni(40123456)).thenReturn(Optional.of(cajeroExistente(true)));

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class,
				() -> servicio.alta(formCajero(), misUnidades));

		assertEquals("dni", e.getCampo());
		assertTrue(e.getMessage().contains("Ya existe"));
	}

	@Test
	void altaConEmailRepetidoSeRechaza() {
		when(personalRepository.findByEmailIgnoreCase("sofia.martinez@epicentro.com"))
				.thenReturn(Optional.of(cajeroExistente(true)));

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class,
				() -> servicio.alta(formCajero(), misUnidades));

		assertEquals("email", e.getCampo());
	}

	@Test
	void altaConEmailDeUnUsuarioExistenteSeRechaza() {
		when(usuarioRepository.existsByUsername("sofia.martinez@epicentro.com")).thenReturn(true);

		assertThrows(ReglaDeNegocioException.class, () -> servicio.alta(formCajero(), misUnidades));
		verify(personalRepository, never()).save(any());
	}

	@Test
	void siElDniEsDeUnEmpleadoDadoDeBajaSeSugiereReactivarlo() {
		Cajero deBaja = cajeroExistente(false);
		when(personalRepository.findByDni(40123456)).thenReturn(Optional.of(deBaja));
		empleadoEnMisUnidades(deBaja);

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class,
				() -> servicio.alta(formCajero(), misUnidades));

		assertTrue(e.getMessage().contains("Reactivalo"));
	}

	@Test
	void altaDeCajeroSinTurnoSeRechaza() {
		EmpleadoForm f = formCajero();
		f.setTurno(null);

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class, () -> servicio.alta(f, misUnidades));

		assertEquals("turno", e.getCampo());
	}

	@Test
	void altaDeCocinaSinRolSeRechaza() {
		EmpleadoForm f = formCajero();
		f.setTipo(TipoEmpleado.COCINA);

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class, () -> servicio.alta(f, misUnidades));

		assertEquals("rolCocina", e.getCampo());
	}

	@Test
	void siFallaElMailNoSeDaDeAltaYSePropagaLaExcepcion() {
		doThrow(new MailSendException("SMTP caido")).when(mailService).enviar(anyString(), anyString(), anyString());

		// la excepcion hace que la transaccion del servicio se deshaga (RuntimeException)
		assertThrows(MailSendException.class, () -> servicio.alta(formCajero(), misUnidades));
	}

	@Test
	void altaEnUnaUnidadAjenaSeRechaza() {
		EmpleadoForm f = formCajero();
		f.setUnidadId(99L);

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class, () -> servicio.alta(f, misUnidades));

		assertEquals("unidadId", e.getCampo());
	}

	@Test
	void conVariasUnidadesHayQueElegirUna() {
		UnidadVenta otra = new FoodTruck();
		otra.setId(2L);

		assertThrows(ReglaDeNegocioException.class, () -> servicio.alta(formCajero(), List.of(unidad, otra)));
	}

	// ------------------------------------------------------------------ baja y reactivacion

	@Test
	void bajaLogicaDesactivaAlEmpleadoYSuUsuario() {
		Cajero empleado = cajeroExistente(true);
		empleadoEnMisUnidades(empleado);
		Usuario usuario = new Usuario();
		usuario.setEnabled(true);
		when(usuarioRepository.findByPersonalId(11L)).thenReturn(Optional.of(usuario));

		servicio.darDeBaja(11L, misUnidades);

		assertFalse(empleado.isActivo());
		assertFalse(usuario.isEnabled());
		verify(personalRepository, never()).delete(any());
		verify(personalRepository, never()).deleteById(any());
	}

	@Test
	void reactivarVuelveAActivarAlEmpleadoYSuUsuario() {
		Cajero empleado = cajeroExistente(false);
		empleadoEnMisUnidades(empleado);
		Usuario usuario = new Usuario();
		usuario.setEnabled(false);
		when(usuarioRepository.findByPersonalId(11L)).thenReturn(Optional.of(usuario));

		servicio.reactivar(11L, misUnidades);

		assertTrue(empleado.isActivo());
		assertTrue(usuario.isEnabled());
	}

	@Test
	void noSePuedeDarDeBajaAUnoQueYaEstaDeBaja() {
		empleadoEnMisUnidades(cajeroExistente(false));

		assertThrows(ReglaDeNegocioException.class, () -> servicio.darDeBaja(11L, misUnidades));
	}

	@Test
	void noSePuedeTocarUnEmpleadoDeOtraUnidad() {
		// el repositorio no lo encuentra entre mis unidades
		when(personalRepository.findEmpleadoDeUnidades(eq(77L), anyCollection())).thenReturn(Optional.empty());

		assertThrows(ReglaDeNegocioException.class, () -> servicio.darDeBaja(77L, misUnidades));
		assertThrows(ReglaDeNegocioException.class, () -> servicio.reactivar(77L, misUnidades));
		assertThrows(ReglaDeNegocioException.class, () -> servicio.obtenerParaEditar(77L, misUnidades));
	}

	// ------------------------------------------------------------------ modificacion

	@Test
	void modificarActualizaLosDatosYSiCambiaElEmailTambienElUsuario() {
		Cajero empleado = cajeroExistente(true);
		empleadoEnMisUnidades(empleado);
		Usuario usuario = new Usuario();
		usuario.setUsername("nahuel.lopez@epicentro.com");
		when(usuarioRepository.findByPersonalId(11L)).thenReturn(Optional.of(usuario));

		EmpleadoForm f = formCajero();
		f.setDni(38111000);
		f.setEmail("nahuel.nuevo@epicentro.com");
		f.setTurno(Turno.MANANA);

		servicio.modificar(11L, f, misUnidades);

		assertEquals("Sofia", empleado.getNombre());
		assertEquals(Turno.MANANA, empleado.getTurno());
		assertEquals("nahuel.nuevo@epicentro.com", empleado.getEmail());
		assertEquals("nahuel.nuevo@epicentro.com", usuario.getUsername());
	}

	@Test
	void modificarNoCambiaElTipoDelEmpleado() {
		Cajero empleado = cajeroExistente(true);
		empleadoEnMisUnidades(empleado);
		EmpleadoForm f = formCajero();
		f.setDni(38111000);
		f.setEmail("nahuel.lopez@epicentro.com");
		f.setTipo(TipoEmpleado.COCINA);
		f.setRolCocina(RolCocina.COCINERO);

		servicio.modificar(11L, f, misUnidades);

		assertEquals(TipoEmpleado.CAJERO, f.getTipo());
		assertEquals(Turno.NOCHE, empleado.getTurno());
	}

	@Test
	void modificarNoPermiteElDniDeOtroEmpleado() {
		Cajero empleado = cajeroExistente(true);
		empleadoEnMisUnidades(empleado);
		Cajero otro = cajeroExistente(true);
		otro.setId(12L);
		when(personalRepository.findByDni(40123456)).thenReturn(Optional.of(otro));

		ReglaDeNegocioException e = assertThrows(ReglaDeNegocioException.class,
				() -> servicio.modificar(11L, formCajero(), misUnidades));

		assertEquals("dni", e.getCampo());
	}

	@Test
	void modificarConElMismoDniYEmailNoEsDuplicado() {
		Cajero empleado = cajeroExistente(true);
		empleadoEnMisUnidades(empleado);
		when(personalRepository.findByDni(38111000)).thenReturn(Optional.of(empleado));
		when(personalRepository.findByEmailIgnoreCase("nahuel.lopez@epicentro.com")).thenReturn(Optional.of(empleado));
		EmpleadoForm f = formCajero();
		f.setDni(38111000);
		f.setEmail("nahuel.lopez@epicentro.com");

		servicio.modificar(11L, f, misUnidades);

		assertEquals("Sofia", empleado.getNombre());
	}

	@Test
	void noSePuedeModificarUnEmpleadoDadoDeBaja() {
		empleadoEnMisUnidades(cajeroExistente(false));

		assertThrows(ReglaDeNegocioException.class, () -> servicio.modificar(11L, formCajero(), misUnidades));
	}

	// ------------------------------------------------------------------ lista

	@Test
	void listarSinUnidadesDevuelveVacio() {
		assertTrue(servicio.listar(List.of(), true).isEmpty());
		verify(personalRepository, never()).findEmpleadosDeUnidades(anyCollection(), eq(true));
	}

	@Test
	void listarMuestraTipoYDetalleDeCadaEmpleado() {
		Cajero cajero = cajeroExistente(true);
		Cocinero cocinero = new Cocinero();
		cocinero.setId(3L);
		cocinero.setNombre("Marcos");
		cocinero.setApellido("Diaz");
		cocinero.setDni(33111222);
		cocinero.setFechaNacimiento(LocalDate.of(1992, 7, 12));
		cocinero.setFechaIngreso(LocalDate.of(2025, 1, 10));
		cocinero.setEmail("marcos.diaz@epicentro.com");
		cocinero.setRolCocina(RolCocina.COCINERO_AYUDANTE);
		unidad.getPersonal().add(cajero);
		unidad.getPersonal().add(cocinero);
		cajero.getUnidades().add(unidad);
		cocinero.getUnidades().add(unidad);
		when(personalRepository.findEmpleadosDeUnidades(anyCollection(), eq(true)))
				.thenReturn(List.of(cocinero, cajero));

		var lista = servicio.listar(misUnidades, true);

		assertEquals(2, lista.size());
		assertEquals("Cocina", lista.get(0).getTipo());
		assertEquals("Cocinero ayudante", lista.get(0).getDetalle());
		assertEquals("Cajero", lista.get(1).getTipo());
		assertEquals("Turno noche", lista.get(1).getDetalle());
		assertEquals("El Fueguito", lista.get(1).getUnidad());
	}
}
