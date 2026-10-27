package com.unla.epicentrogourmet.services.implementation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.unla.epicentrogourmet.dtos.RegistroUnidadForm;
import com.unla.epicentrogourmet.entities.Cajero;
import com.unla.epicentrogourmet.entities.Cocinero;
import com.unla.epicentrogourmet.entities.Festival;
import com.unla.epicentrogourmet.entities.FoodTruck;
import com.unla.epicentrogourmet.entities.Personal;
import com.unla.epicentrogourmet.entities.PuestoDesarmable;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.enums.Rol;
import com.unla.epicentrogourmet.enums.TipoEmpleado;
import com.unla.epicentrogourmet.repositories.IPersonalRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;
import com.unla.epicentrogourmet.services.IRegistroUnidadService;
import com.unla.epicentrogourmet.services.IFestivalService;

@Service
public class RegistroUnidadService implements IRegistroUnidadService {

	private final IFestivalService festivalService;
	private final IUnidadVentaRepository unidadVentaRepository;
	private final IPersonalRepository personalRepository;
	private final IUsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistroUnidadService(IFestivalService festivalService,
			IUnidadVentaRepository unidadVentaRepository, IPersonalRepository personalRepository,
			IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
		this.festivalService = festivalService;
		this.unidadVentaRepository = unidadVentaRepository;
		this.personalRepository = personalRepository;
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void registrar(RegistroUnidadForm form) {
		String codigo = form.getCodigo().trim();
		if (!codigo.matches("^[A-Z]{2}[0-9]{8}$")) {
			throw new IllegalArgumentException("El código debe tener 2 letras mayúsculas y 8 dígitos");
		}
		if (unidadVentaRepository.existsByCodigo(codigo)) {
			throw new IllegalArgumentException("Ya existe una unidad con ese código");
		}
		String username = form.getUsername().trim();
		if (usuarioRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("Ese nombre de usuario ya está registrado");
		}
		String email = form.getEmail().trim();
		if (personalRepository.findByEmailIgnoreCase(email).isPresent()) {
			throw new IllegalArgumentException("Ese email ya está asociado a una persona");
		}
		if (personalRepository.findByDni(form.getDni()).isPresent()) {
			throw new IllegalArgumentException("Ya existe una persona con ese DNI");
		}

		Festival festival = festivalService.findById(form.getFestivalId())
				.orElseThrow(() -> new IllegalArgumentException("El festival seleccionado no existe"));
		festivalService.validarPeriodo(festival.getFechaInicio(), festival.getFechaFin());
		Personal responsable = crearResponsable(form, email);
		if (!responsable.esMayorDeEdad()) {
			throw new IllegalArgumentException("El responsable debe ser mayor de edad");
		}
		if (form.getFechaIngreso().isBefore(form.getFechaNacimiento())) {
			throw new IllegalArgumentException("La fecha de ingreso no puede ser anterior al nacimiento");
		}

		responsable = personalRepository.save(responsable);
		UnidadVenta unidad = crearUnidad(form, codigo, festival);
		unidad.setResponsable(responsable);
		unidad.getPersonal().add(responsable);
		unidadVentaRepository.save(unidad);

		Usuario usuario = new Usuario();
		usuario.setUsername(username);
		usuario.setPassword(passwordEncoder.encode(form.getPassword()));
		usuario.setRol(Rol.RESPONSABLE);
		usuario.setPersonal(responsable);
		usuarioRepository.save(usuario);
	}

	private Personal crearResponsable(RegistroUnidadForm form, String email) {
		Personal persona;
		if (form.getTipoEmpleado() == TipoEmpleado.CAJERO) {
			if (form.getTurno() == null) {
				throw new IllegalArgumentException("Seleccioná el turno del responsable cajero");
			}
			Cajero cajero = new Cajero();
			cajero.setTurno(form.getTurno());
			persona = cajero;
		} else {
			if (form.getRolCocina() == null) {
				throw new IllegalArgumentException("Seleccioná el rol de cocina del responsable");
			}
			Cocinero cocinero = new Cocinero();
			cocinero.setRolCocina(form.getRolCocina());
			persona = cocinero;
		}
		persona.setNombre(form.getNombre().trim());
		persona.setApellido(form.getApellido().trim());
		persona.setDni(form.getDni());
		persona.setFechaNacimiento(form.getFechaNacimiento());
		persona.setFechaIngreso(form.getFechaIngreso());
		persona.setEmail(email);
		persona.setContacto(StringUtils.hasText(form.getContacto()) ? form.getContacto().trim() : null);
		persona.setActivo(true);
		return persona;
	}

	private UnidadVenta crearUnidad(RegistroUnidadForm form, String codigo, Festival festival) {
		UnidadVenta unidad;
		if ("FOOD_TRUCK".equals(form.getTipoUnidad())) {
			if (!StringUtils.hasText(form.getPatente())) {
				throw new IllegalArgumentException("Ingresá la patente del Food Truck");
			}
			FoodTruck foodTruck = new FoodTruck();
			foodTruck.setPatente(form.getPatente().trim());
			foodTruck.setRequiereElectricidad(form.isRequiereElectricidad());
			unidad = foodTruck;
		} else if ("PUESTO".equals(form.getTipoUnidad())) {
			if (form.getCantidadCarpas() == null || form.getCantidadCarpas() <= 0) {
				throw new IllegalArgumentException("La cantidad de carpas debe ser mayor a cero");
			}
			if (form.getTiempoMontaje() == null || form.getTiempoMontaje() < 0) {
				throw new IllegalArgumentException("El tiempo de montaje no puede ser negativo");
			}
			PuestoDesarmable puesto = new PuestoDesarmable();
			puesto.setCantidadCarpas(form.getCantidadCarpas());
			puesto.setTiempoMontaje(form.getTiempoMontaje());
			unidad = puesto;
		} else {
			throw new IllegalArgumentException("Seleccioná un tipo de unidad válido");
		}
		unidad.setNombreComercial(form.getNombreComercial().trim());
		unidad.setSuperficie(form.getSuperficie());
		unidad.setCodigo(codigo);
		unidad.setFestival(festival);
		return unidad;
	}
}
