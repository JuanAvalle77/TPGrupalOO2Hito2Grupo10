package com.unla.epicentrogourmet.services.implementation;

import com.unla.epicentrogourmet.dtos.RegistroUnidadDTO;
import com.unla.epicentrogourmet.entities.*;
import com.unla.epicentrogourmet.enums.Rol;
import com.unla.epicentrogourmet.repositories.IFestivalRepository;
import com.unla.epicentrogourmet.repositories.IPersonalRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {

@Autowired private IUnidadVentaRepository unidadRepository;
@Autowired private IPersonalRepository personalRepository;
@Autowired private IUsuarioRepository usuarioRepository;
@Autowired private IFestivalRepository festivalRepository;
@Autowired private PasswordEncoder passwordEncoder;

@Transactional
public void registrarUnidadConResponsable(RegistroUnidadDTO dto)throws Exception{

  if(unidadRepository.existsByCodigo(dto.getCodigo())) {

      throw new Exception("El código de unidad ya se encuentra registrado");
  }
  if(usuarioRepository.existsByUsername(dto.getUserName())){

      throw new Exception("El nombre de usuario ya se encuentra registrado");
  }
    Personal responsable;

  if("CAJERO".equalsIgnoreCase(dto.getTipoPersonal())){

      responsable=new Cajero();
  }
  else{
      responsable=new Cocinero();
  }
  responsable.setNombre(dto.getNombre());
  responsable.setApellido(dto.getApellido());
  responsable.setDni(dto.getDni());
  responsable.setFechaNacimiento(dto.getFechaNacimiento());
  responsable.setFechaIngreso(dto.getFechaIngreso());
  responsable.setEmail(dto.getEmail());
  responsable=personalRepository.save(responsable);

  Usuario usuario =new Usuario();
    usuario.setUsername(dto.getUserName());
    usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
    usuario.setRol(Rol.RESPONSABLE);
    usuario.setPersonal(responsable);
    usuarioRepository.save(usuario);

    UnidadVenta unidad;
    if("FOOD_TRUCK".equalsIgnoreCase(dto.getTipoUnidad())){

        FoodTruck ft=new FoodTruck();
        ft.setPatente(dto.getPatente());
        ft.setRequiereElectricidad(Boolean.TRUE.equals(dto.getRequiereElectricidad()));
        unidad=ft;
    }
    else{
    PuestoDesarmable pd=new PuestoDesarmable();
    pd.setCantidadCarpas(dto.getCarpas());
    pd.setTiempoMontaje(dto.getMinutosMontaje());
    unidad=pd;
    }

    Festival festival=festivalRepository.findById(dto.getFestivalId())
            .orElseThrow(()->new Exception("El festival seleccionado no existe"));


    unidad.setFestival(festival);

    unidad.setNombreComercial(dto.getNombreComercial());
    unidad.setSuperficie(dto.getSuperficie());
    unidad.setCodigo(dto.getCodigo());

    unidad.setResponsable(responsable);
    unidad.getPersonal().add(responsable);
    unidadRepository.save(unidad);


}


}
