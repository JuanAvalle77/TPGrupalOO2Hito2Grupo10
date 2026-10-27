package com.unla.epicentrogourmet.services.implementation;

import com.unla.epicentrogourmet.entities.Plato;
import com.unla.epicentrogourmet.entities.UnidadVenta;
import com.unla.epicentrogourmet.entities.Usuario;
import com.unla.epicentrogourmet.repositories.IPlatoRepository;
import com.unla.epicentrogourmet.repositories.IUnidadVentaRepository;
import com.unla.epicentrogourmet.repositories.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class PlatoService {
    @Autowired private IPlatoRepository platoRepository;
    @Autowired private IUnidadVentaRepository unidadVentaRepository;
    @Autowired private IUsuarioRepository usuarioRepository;

    public List<Plato> obtenerPlatosPorUsername(String username) throws Exception {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Long idPersonal = usuario.getPersonal().getId();

        List<UnidadVenta> unidades = unidadVentaRepository.findByResponsableId(idPersonal);

        if (unidades.isEmpty()) {
            throw new Exception("El responsable no tiene una unidad asignada");
        }

        UnidadVenta unidad = unidades.get(0);

        List<Plato> platos = platoRepository.findByUnidadVentaId(unidad.getId());


        return platos;
    }

    @Transactional
    public void guardarPlatoParaResponsable(Plato plato, String username) throws Exception {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<UnidadVenta> unidades = unidadVentaRepository.findByResponsableId(usuario.getPersonal().getId());

        if (unidades.isEmpty()) {
            throw new Exception("El responsable no tiene una unidad asignada");
        }

        UnidadVenta unidad = unidades.get(0);

        plato.setUnidadVenta(unidad);
        platoRepository.save(plato);
    }



}
