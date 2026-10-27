package com.unla.epicentrogourmet.controllers;

import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unla.epicentrogourmet.dtos.EmpleadoForm;
import com.unla.epicentrogourmet.dtos.FiltroEmpleadosForm;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.enums.RolCocina;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.enums.Turno;
import com.unla.epicentrogourmet.exceptions.ReglaDeNegocioException;
import com.unla.epicentrogourmet.services.IEmpleadoService;
import com.unla.epicentrogourmet.services.implementation.SesionService;

import jakarta.validation.Valid;

/** CU ABM de empleados: el responsable gestiona los empleados de su(s) unidad(es). */
@Controller
@RequestMapping("/responsable/empleados")
public class GestionEmpleadosController {

	private static final String VISTA_LISTA = "responsable/empleados";
	private static final String VISTA_FORMULARIO = "responsable/empleado-form";
	private static final String REDIRECT_LISTA = "redirect:/responsable/empleados";

	private final IEmpleadoService empleadoService;
	private final SesionService sesionService;

	public GestionEmpleadosController(IEmpleadoService empleadoService, SesionService sesionService) {
		this.empleadoService = empleadoService;
		this.sesionService = sesionService;
	}

	@GetMapping("")
	public String lista(@Valid @ModelAttribute("filtro") FiltroEmpleadosForm filtro, BindingResult result, Model model) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		model.addAttribute("empleados", result.hasErrors() ? List.of()
				: empleadoService.filtrar(unidades, filtro));
		model.addAttribute("bajas", empleadoService.listar(unidades, false));
		model.addAttribute("tiposFiltro", List.of("Cajero", "Cocina"));
		model.addAttribute("detallesFiltro", List.of("Mañana", "Noche", "Cocinero", "Cocinero ayudante", "Lavaplatos"));
		model.addAttribute("filtroErrores", result.getFieldErrors());
		return VISTA_LISTA;
	}

	// ---------------------------------------------------------------- alta

	@GetMapping("/nuevo")
	public String nuevo(Model model) {
		return formulario(model, new EmpleadoForm(), false);
	}

	@PostMapping("/nuevo")
	public String guardar(@Valid @ModelAttribute("empleadoForm") EmpleadoForm form, BindingResult result,
			Model model, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return formulario(model, form, false);
		}
		try {
			Personal alta = empleadoService.alta(form, sesionService.getUnidadesDelResponsable());
			redirect.addFlashAttribute("ok", "Se dio de alta a " + alta.getNombreCompleto()
					+ ". Le enviamos un mail a " + alta.getEmail() + " con su usuario y contraseña.");
			return REDIRECT_LISTA;
		} catch (ReglaDeNegocioException e) {
			agregarError(result, e);
		} catch (MailException e) {
			result.reject("mail", "No se pudo enviar el mail al empleado, por eso no se dio de alta. "
					+ "Revisá el email e intentá de nuevo.");
		}
		return formulario(model, form, false);
	}

	// ---------------------------------------------------------------- modificacion

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model, RedirectAttributes redirect) {
		try {
			return formulario(model,
					empleadoService.obtenerParaEditar(id, sesionService.getUnidadesDelResponsable()), true);
		} catch (ReglaDeNegocioException e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return REDIRECT_LISTA;
		}
	}

	@PostMapping("/{id}/editar")
	public String modificar(@PathVariable Long id, @Valid @ModelAttribute("empleadoForm") EmpleadoForm form,
			BindingResult result, Model model, RedirectAttributes redirect) {
		form.setId(id);
		if (result.hasErrors()) {
			return formulario(model, form, true);
		}
		try {
			empleadoService.modificar(id, form, sesionService.getUnidadesDelResponsable());
			redirect.addFlashAttribute("ok", "Se guardaron los cambios de " + form.getApellido() + ", "
					+ form.getNombre() + ".");
			return REDIRECT_LISTA;
		} catch (ReglaDeNegocioException e) {
			agregarError(result, e);
		}
		return formulario(model, form, true);
	}

	// ---------------------------------------------------------------- baja logica y reactivacion

	@PostMapping("/baja/{id}")
	public String baja(@PathVariable Long id, RedirectAttributes redirect) {
		try {
			empleadoService.darDeBaja(id, sesionService.getUnidadesDelResponsable());
			redirect.addFlashAttribute("ok", "Empleado dado de baja. Ya no puede iniciar sesión; "
					+ "lo podés reactivar desde la lista de empleados dados de baja.");
		} catch (ReglaDeNegocioException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}
		return REDIRECT_LISTA;
	}

	@PostMapping("/reactivar/{id}")
	public String reactivar(@PathVariable Long id, RedirectAttributes redirect) {
		try {
			empleadoService.reactivar(id, sesionService.getUnidadesDelResponsable());
			redirect.addFlashAttribute("ok", "Empleado reactivado: vuelve a figurar en la lista y puede iniciar sesión.");
		} catch (ReglaDeNegocioException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}
		return REDIRECT_LISTA;
	}

	// ---------------------------------------------------------------- auxiliares

	private String formulario(Model model, EmpleadoForm form, boolean edicion) {
		List<UnidadVenta> unidades = sesionService.getUnidadesDelResponsable();
		model.addAttribute("empleadoForm", form);
		model.addAttribute("edicion", edicion);
		model.addAttribute("unidades", unidades);
		model.addAttribute("tipos", TipoEmpleado.values());
		model.addAttribute("turnos", Turno.values());
		model.addAttribute("rolesCocina", RolCocina.values());
		return VISTA_FORMULARIO;
	}

	/** El error de negocio se muestra junto al campo, o arriba del formulario si no es de un campo. */
	private void agregarError(BindingResult result, ReglaDeNegocioException e) {
		if (e.getCampo() != null) {
			result.rejectValue(e.getCampo(), "regla", e.getMessage());
		} else {
			result.reject("regla", e.getMessage());
		}
	}
}
