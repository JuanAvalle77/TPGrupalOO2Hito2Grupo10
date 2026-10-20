package com.unla.epicentrogourmet.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSendException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.unla.epicentrogourmet.configuration.SecurityConfiguration;
import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.dtos.EmpleadoForm;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.FoodTruck;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.enums.Turno;
import com.unla.epicentrogourmet.exceptions.ReglaDeNegocioException;
import com.unla.epicentrogourmet.services.IEmpleadoService;
import com.unla.epicentrogourmet.services.implementation.SesionService;
import com.unla.epicentrogourmet.services.implementation.UserService;

/** Renderiza las vistas reales del ABM (sin base de datos) y verifica la seguridad por rol. */
@WebMvcTest(GestionEmpleadosController.class)
@Import(SecurityConfiguration.class)
class GestionEmpleadosControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IEmpleadoService empleadoService;
	@MockBean
	private SesionService sesionService;
	@MockBean
	private UserService userService;

	private UnidadVenta unidad;

	@BeforeEach
	void setUp() {
		unidad = new FoodTruck();
		unidad.setId(1L);
		unidad.setNombreComercial("El Fueguito");
		when(sesionService.getUnidadesDelResponsable()).thenReturn(List.of(unidad));
	}

	private EmpleadoDTO dto(Long id, String nombre) {
		return new EmpleadoDTO(id, nombre, 30111222, "x@epicentro.com", "1155550001", "Cajero", "Turno noche",
				LocalDate.of(2023, 3, 1), 3, "El Fueguito");
	}

	/** Parametros validos del formulario de alta. */
	private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder altaValida() {
		return post("/responsable/empleados/nuevo").with(csrf())
				.param("nombre", "Sofia").param("apellido", "Martinez").param("dni", "40123456")
				.param("fechaNacimiento", "2000-01-15").param("fechaIngreso", "2026-01-10")
				.param("email", "sofia.martinez@epicentro.com").param("contacto", "1155559999")
				.param("tipo", "CAJERO").param("turno", "NOCHE");
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void laListaMuestraActivosYDadosDeBaja() throws Exception {
		when(empleadoService.listar(any(), eq(true))).thenReturn(List.of(dto(2L, "Gomez, Lucia")));
		when(empleadoService.listar(any(), eq(false))).thenReturn(List.of(dto(11L, "Lopez, Nahuel")));

		mockMvc.perform(get("/responsable/empleados")).andExpect(status().isOk())
				.andExpect(view().name("responsable/empleados"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Gomez, Lucia")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Dar de baja")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lopez, Nahuel")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Reactivar")));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void elFormularioDeAltaSeMuestraConSusCampos() throws Exception {
		mockMvc.perform(get("/responsable/empleados/nuevo")).andExpect(status().isOk())
				.andExpect(view().name("responsable/empleado-form"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Nuevo empleado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("name=\"email\"")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Mañana")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Cocinero ayudante")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El Fueguito")));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void altaValidaRedirigeALaListaConMensaje() throws Exception {
		Personal alta = new Cajero();
		alta.setNombre("Sofia");
		alta.setApellido("Martinez");
		alta.setEmail("sofia.martinez@epicentro.com");
		when(empleadoService.alta(any(EmpleadoForm.class), any())).thenReturn(alta);

		mockMvc.perform(altaValida()).andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/responsable/empleados")).andExpect(flash().attributeExists("ok"));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void altaConCamposInvalidosVuelveAlFormularioSinLlamarAlServicio() throws Exception {
		mockMvc.perform(post("/responsable/empleados/nuevo").with(csrf()).param("nombre", "").param("email", "no-es-un-mail")
				.param("dni", "123").param("tipo", "CAJERO"))
				.andExpect(status().isOk()).andExpect(view().name("responsable/empleado-form"))
				.andExpect(model().attributeHasFieldErrors("empleadoForm", "nombre", "email", "dni",
						"fechaNacimiento", "fechaIngreso"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El email no tiene un formato válido.")));

		verify(empleadoService, never()).alta(any(), any());
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void laReglaDeNegocioSeMuestraJuntoAlCampo() throws Exception {
		when(empleadoService.alta(any(EmpleadoForm.class), any()))
				.thenThrow(new ReglaDeNegocioException("fechaNacimiento", "El empleado debe ser mayor de edad."));

		mockMvc.perform(altaValida()).andExpect(status().isOk()).andExpect(view().name("responsable/empleado-form"))
				.andExpect(model().attributeHasFieldErrors("empleadoForm", "fechaNacimiento"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("El empleado debe ser mayor de edad.")));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void siFallaElMailSeAvisaYNoSeDaDeAlta() throws Exception {
		when(empleadoService.alta(any(EmpleadoForm.class), any())).thenThrow(new MailSendException("SMTP caido"));

		mockMvc.perform(altaValida()).andExpect(status().isOk()).andExpect(view().name("responsable/empleado-form"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("No se pudo enviar el mail")));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void elFormularioDeEdicionVieneCargadoConElTipoFijo() throws Exception {
		EmpleadoForm form = new EmpleadoForm();
		form.setId(2L);
		form.setNombre("Lucia");
		form.setApellido("Gomez");
		form.setDni(32555111);
		form.setFechaNacimiento(LocalDate.of(1995, 3, 20));
		form.setFechaIngreso(LocalDate.of(2025, 6, 1));
		form.setEmail("lucia.gomez@epicentro.com");
		form.setTipo(TipoEmpleado.CAJERO);
		form.setTurno(Turno.NOCHE);
		when(empleadoService.obtenerParaEditar(eq(2L), any())).thenReturn(form);

		mockMvc.perform(get("/responsable/empleados/2/editar")).andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Modificar empleado")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("lucia.gomez@epicentro.com")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("type=\"hidden\" name=\"tipo\"")));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void editarUnEmpleadoAjenoVuelveALaListaConError() throws Exception {
		when(empleadoService.obtenerParaEditar(eq(77L), any()))
				.thenThrow(new ReglaDeNegocioException("No encontramos ese empleado en tus unidades."));

		mockMvc.perform(get("/responsable/empleados/77/editar")).andExpect(redirectedUrl("/responsable/empleados"))
				.andExpect(flash().attributeExists("error"));
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void bajaYReactivacionLlamanAlServicioYRedirigen() throws Exception {
		mockMvc.perform(post("/responsable/empleados/baja/2").with(csrf())).andExpect(redirectedUrl("/responsable/empleados"))
				.andExpect(flash().attributeExists("ok"));
		mockMvc.perform(post("/responsable/empleados/reactivar/11").with(csrf()))
				.andExpect(redirectedUrl("/responsable/empleados")).andExpect(flash().attributeExists("ok"));

		verify(empleadoService).darDeBaja(eq(2L), any());
		verify(empleadoService).reactivar(eq(11L), any());
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void siLaBajaFallaSeMuestraElError() throws Exception {
		doThrow(new ReglaDeNegocioException("El empleado ya está dado de baja.")).when(empleadoService)
				.darDeBaja(eq(2L), any());

		mockMvc.perform(post("/responsable/empleados/baja/2").with(csrf())).andExpect(flash().attributeExists("error"));
	}

	// ------------------------------------------------------------------ seguridad

	@Test
	@WithMockUser(roles = "EMPLEADO")
	void unEmpleadoNoPuedeEntrarAlAbm() throws Exception {
		mockMvc.perform(get("/responsable/empleados")).andExpect(status().isForbidden());
		mockMvc.perform(post("/responsable/empleados/baja/2").with(csrf())).andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void unAdminTampocoPuedeEntrarAlAbm() throws Exception {
		mockMvc.perform(get("/responsable/empleados/nuevo")).andExpect(status().isForbidden());
	}

	@Test
	void sinSesionRedirigeAlLogin() throws Exception {
		mockMvc.perform(get("/responsable/empleados")).andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(roles = "RESPONSABLE")
	void unPostSinTokenCsrfSeRechaza() throws Exception {
		mockMvc.perform(post("/responsable/empleados/baja/2")).andExpect(status().isForbidden());
		verify(empleadoService, never()).darDeBaja(any(), any());
	}
}
