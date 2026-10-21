package com.unla.epicentrogourmet.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Usuario;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsername(String username);

	boolean existsByUsername(String username);

	/** Usuario de login de un empleado. */
	Optional<Usuario> findByPersonalId(Long personalId);
}
