package com.unla.epicentrogourmet.services.implementation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unla.epicentrogourmet.dtos.EmpleadoDTO;
import com.unla.epicentrogourmet.dtos.EmpleadoForm;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.enums.Rol;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.exceptions.ReglaDeNegocioException;
import com.unla.epicentrogourmet.helpers.ContraseniaAleatoria;
import com.unla.epicentrogourmet.helpers.EtiquetasHelper;
import com.unla.epicentrogourmet.repositories.IPersonalRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;
import com.unla.epicentrogourmet.services.IEmpleadoService;

/** CU ABM de empleados: alta con usuario y mail, modificacion, baja logica y reactivacion. */
@Service("empleadoService")
public class EmpleadoService implements IEmpleadoService {

	private static final String ASUNTO_ALTA = "Alta en Epicentro Gourmet";

	private final IPersonalRepository personalRepository;
	private final IUsuarioRepository usuarioRepository;
	private final IUnidadVentaRepository unidadVentaRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	public EmpleadoService(IPersonalRepository personalRepository, IUsuarioRepository usuarioRepository,
			IUnidadVentaRepository unidadVentaRepository, PasswordEncoder passwordEncoder,
			MailService mailService) {
		this.personalRepository = personalRepository;
		this.usuarioRepository = usuarioRepository;
		this.unidadVentaRepository = unidadVentaRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmpleadoDTO> listar(List<UnidadVenta> misUnidades, boolean activos) {
		if (misUnidades.isEmpty()) {
			return List.of();
		}
		return personalRepository.findEmpleadosDeUnidades(ids(misUnidades), activos).stream()
				.map(p -> aDTO(p, misUnidades)).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public EmpleadoForm obtenerParaEditar(Long id, List<UnidadVenta> misUnidades) {
		Personal p = sinProxy(empleadoDeMisUnidades(id, misUnidades));
		EmpleadoForm form = new EmpleadoForm();
		form.setId(p.getId());
		form.setUnidadId(unidadDe(p, misUnidades).map(UnidadVenta::getId).orElse(null));
		form.setNombre(p.getNombre());
		form.setApellido(p.getApellido());
		form.setDni(p.getDni());
		form.setFechaNacimiento(p.getFechaNacimiento());
		form.setFechaIngreso(p.getFechaIngreso());
		form.setEmail(p.getEmail());
		form.setContacto(p.getContacto());
		if (p instanceof Cajero cajero) {
			form.setTipo(TipoEmpleado.CAJERO);
			form.setTurno(cajero.getTurno());
		} else if (p instanceof Cocinero cocinero) {
			form.setTipo(TipoEmpleado.COCINA);
			form.setRolCocina(cocinero.getRolCocina());
		}
		return form;
	}

	@Override
	@Transactional
	public Personal alta(EmpleadoForm form, List<UnidadVenta> misUnidades) {
		UnidadVenta unidad = unidadElegida(form.getUnidadId(), misUnidades);
		validarCarga(form, null, misUnidades);

		Personal nuevo = form.getTipo() == TipoEmpleado.CAJERO ? new Cajero() : new Cocinero();
		copiarDatos(nuevo, form);
		validarMayorDeEdad(nuevo);
		nuevo.setActivo(true);

		Personal guardado = personalRepository.save(nuevo);
		unidad.getPersonal().add(guardado);
		unidadVentaRepository.save(unidad);

		String contrasenia = ContraseniaAleatoria.generar();
		Usuario usuario = new Usuario();
		usuario.setUsername(guardado.getEmail());
		usuario.setPassword(passwordEncoder.encode(contrasenia));
		usuario.setRol(Rol.EMPLEADO);
		usuario.setEnabled(true);
		usuario.setPersonal(guardado);
		usuarioRepository.save(usuario);

		// Ultimo paso: si el mail falla se lanza la excepcion y se deshace todo el alta
		// (la contrasenia solo existe en ese mail, no tendria sentido dejar el usuario creado).
		mailService.enviar(guardado.getEmail(), ASUNTO_ALTA, textoDeAlta(guardado, unidad, contrasenia));
		return guardado;
	}

	@Override
	@Transactional
	public void modificar(Long id, EmpleadoForm form, List<UnidadVenta> misUnidades) {
		Personal empleado = sinProxy(empleadoDeMisUnidades(id, misUnidades));
		if (!empleado.isActivo()) {
			throw new ReglaDeNegocioException("El empleado está dado de baja: reactivalo antes de modificarlo.");
		}
		// El tipo no se cambia: Cajero y Cocinero son clases distintas en la base.
		form.setTipo(empleado instanceof Cajero ? TipoEmpleado.CAJERO : TipoEmpleado.COCINA);
		validarCarga(form, empleado, misUnidades);

		String emailAnterior = empleado.getEmail();
		copiarDatos(empleado, form);
		validarMayorDeEdad(empleado);
		personalRepository.save(empleado);

		if (!emailAnterior.equalsIgnoreCase(empleado.getEmail())) {
			// el email es el usuario con el que inicia sesion
			usuarioRepository.findByPersonalId(empleado.getId()).ifPresent(usuario -> {
				usuario.setUsername(empleado.getEmail());
				usuarioRepository.save(usuario);
			});
		}
	}

	@Override
	@Transactional
	public void darDeBaja(Long id, List<UnidadVenta> misUnidades) {
		Personal empleado = empleadoDeMisUnidades(id, misUnidades);
		if (!empleado.isActivo()) {
			throw new ReglaDeNegocioException("El empleado ya está dado de baja.");
		}
		cambiarEstado(empleado, false);
	}

	@Override
	@Transactional
	public void reactivar(Long id, List<UnidadVenta> misUnidades) {
		Personal empleado = empleadoDeMisUnidades(id, misUnidades);
		if (empleado.isActivo()) {
			throw new ReglaDeNegocioException("El empleado ya está activo.");
		}
		cambiarEstado(empleado, true);
	}

	// ---------------------------------------------------------------- privados

	/** Baja o alta logica: el empleado y su usuario cambian juntos. */
	private void cambiarEstado(Personal empleado, boolean activo) {
		empleado.setActivo(activo);
		personalRepository.save(empleado);
		usuarioRepository.findByPersonalId(empleado.getId()).ifPresent(usuario -> {
			usuario.setEnabled(activo);
			usuarioRepository.save(usuario);
		});
	}

	private Personal empleadoDeMisUnidades(Long id, List<UnidadVenta> misUnidades) {
		if (misUnidades.isEmpty()) {
			throw new ReglaDeNegocioException("No tenés unidades de venta a cargo.");
		}
		return personalRepository.findEmpleadoDeUnidades(id, ids(misUnidades))
				.orElseThrow(() -> new ReglaDeNegocioException("No encontramos ese empleado en tus unidades."));
	}

	/** Unidad elegida en el formulario; si el responsable tiene una sola, es esa. */
	private UnidadVenta unidadElegida(Long unidadId, List<UnidadVenta> misUnidades) {
		if (misUnidades.isEmpty()) {
			throw new ReglaDeNegocioException("No tenés unidades de venta a cargo.");
		}
		if (unidadId == null) {
			if (misUnidades.size() == 1) {
				return misUnidades.get(0);
			}
			throw new ReglaDeNegocioException("unidadId", "Elegí la unidad de venta.");
		}
		return misUnidades.stream().filter(u -> u.getId().equals(unidadId)).findFirst()
				.orElseThrow(() -> new ReglaDeNegocioException("unidadId", "Esa unidad no está a tu cargo."));
	}

	private void validarCarga(EmpleadoForm form, Personal existente, List<UnidadVenta> misUnidades) {
		if (form.getTipo() == TipoEmpleado.CAJERO && form.getTurno() == null) {
			throw new ReglaDeNegocioException("turno", "Elegí el turno del cajero.");
		}
		if (form.getTipo() == TipoEmpleado.COCINA && form.getRolCocina() == null) {
			throw new ReglaDeNegocioException("rolCocina", "Elegí el rol en la cocina.");
		}
		if (form.getFechaIngreso().isBefore(form.getFechaNacimiento())) {
			throw new ReglaDeNegocioException("fechaIngreso", "La fecha de ingreso no puede ser anterior al nacimiento.");
		}
		validarDniLibre(form.getDni(), existente, misUnidades);
		validarEmailLibre(normalizarEmail(form.getEmail()), existente, misUnidades);
	}

	private void validarDniLibre(int dni, Personal existente, List<UnidadVenta> misUnidades) {
		personalRepository.findByDni(dni).filter(otro -> !esElMismo(otro, existente)).ifPresent(otro -> {
			throw new ReglaDeNegocioException("dni", mensajeDuplicado("DNI", otro, misUnidades));
		});
	}

	private void validarEmailLibre(String email, Personal existente, List<UnidadVenta> misUnidades) {
		if (existente != null && existente.getEmail().equalsIgnoreCase(email)) {
			return; // no cambio el email
		}
		personalRepository.findByEmailIgnoreCase(email).filter(otro -> !esElMismo(otro, existente))
				.ifPresent(otro -> {
					throw new ReglaDeNegocioException("email", mensajeDuplicado("email", otro, misUnidades));
				});
		if (usuarioRepository.existsByUsername(email)) {
			throw new ReglaDeNegocioException("email", "Ya existe un usuario con ese email.");
		}
	}

	private boolean esElMismo(Personal otro, Personal existente) {
		return existente != null && otro.getId().equals(existente.getId());
	}

	/** Si el duplicado es un empleado dado de baja de mis unidades, se sugiere reactivarlo. */
	private String mensajeDuplicado(String dato, Personal otro, List<UnidadVenta> misUnidades) {
		boolean esMioYEstaDeBaja = !otro.isActivo()
				&& personalRepository.findEmpleadoDeUnidades(otro.getId(), ids(misUnidades)).isPresent();
		if (esMioYEstaDeBaja) {
			return "Ya hay un empleado dado de baja con ese " + dato
					+ ". Reactivalo desde la lista de empleados dados de baja en lugar de cargarlo de nuevo.";
		}
		return "Ya existe un empleado registrado con ese " + dato + ".";
	}

	private void validarMayorDeEdad(Personal personal) {
		if (!personal.esMayorDeEdad()) {
			throw new ReglaDeNegocioException("fechaNacimiento", "El empleado debe ser mayor de edad.");
		}
	}

	private void copiarDatos(Personal personal, EmpleadoForm form) {
		personal.setNombre(form.getNombre().trim());
		personal.setApellido(form.getApellido().trim());
		personal.setDni(form.getDni());
		personal.setFechaNacimiento(form.getFechaNacimiento());
		personal.setFechaIngreso(form.getFechaIngreso());
		personal.setEmail(normalizarEmail(form.getEmail()));
		String contacto = form.getContacto() == null ? "" : form.getContacto().trim();
		personal.setContacto(contacto.isEmpty() ? null : contacto);
		if (personal instanceof Cajero cajero) {
			cajero.setTurno(form.getTurno());
		} else if (personal instanceof Cocinero cocinero) {
			cocinero.setRolCocina(form.getRolCocina());
		}
	}

	private String normalizarEmail(String email) {
		return email.trim().toLowerCase();
	}

	private String textoDeAlta(Personal empleado, UnidadVenta unidad, String contrasenia) {
		return "Hola " + empleado.getNombre() + ",\n\n"
				+ "Te dieron de alta en la unidad de venta \"" + unidad.getNombreComercial()
				+ "\" de Epicentro Gourmet.\n\n"
				+ "Para iniciar sesión usá estos datos:\n"
				+ "  Usuario: " + empleado.getEmail() + "\n"
				+ "  Contraseña: " + contrasenia + "\n\n"
				+ "¡Que tengas un buen festival!";
	}

	private EmpleadoDTO aDTO(Personal personal, List<UnidadVenta> misUnidades) {
		Personal p = sinProxy(personal);
		String tipo = "";
		String detalle = "";
		if (p instanceof Cajero cajero) {
			tipo = TipoEmpleado.CAJERO.getDescripcion();
			detalle = "Turno " + EtiquetasHelper.turno(cajero.getTurno()).toLowerCase();
		} else if (p instanceof Cocinero cocinero) {
			tipo = TipoEmpleado.COCINA.getDescripcion();
			detalle = EtiquetasHelper.rolCocina(cocinero.getRolCocina());
		}
		String unidad = unidadDe(p, misUnidades).map(UnidadVenta::getNombreComercial).orElse("-");
		return new EmpleadoDTO(p.getId(), p.getNombreCompleto(), p.getDni(), p.getEmail(), p.getContacto(), tipo,
				detalle, p.getFechaIngreso(), p.getAntiguedadEnAnios(LocalDate.now()), unidad);
	}

	/** Primera de mis unidades en la que trabaja el empleado. */
	private Optional<UnidadVenta> unidadDe(Personal personal, List<UnidadVenta> misUnidades) {
		Set<Long> idsEmpleado = personal.getUnidades().stream().map(UnidadVenta::getId).collect(Collectors.toSet());
		return misUnidades.stream().filter(u -> idsEmpleado.contains(u.getId())).findFirst();
	}

	private List<Long> ids(List<UnidadVenta> unidades) {
		return unidades.stream().map(UnidadVenta::getId).toList();
	}

	/** Las relaciones lazy pueden devolver un proxy de Personal: aca se necesita la subclase real. */
	private Personal sinProxy(Personal personal) {
		return (Personal) Hibernate.unproxy(personal);
	}
}
