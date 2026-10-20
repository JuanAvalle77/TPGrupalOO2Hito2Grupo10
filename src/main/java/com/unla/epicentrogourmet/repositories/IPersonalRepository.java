package com.unla.epicentrogourmet.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.epicentrogourmet.entities.Personal;

@Repository
public interface IPersonalRepository extends JpaRepository<Personal, Long> {

	Optional<Personal> findByDni(int dni);

	Optional<Personal> findByEmailIgnoreCase(String email);

	/**
	 * Empleados de las unidades indicadas, activos o dados de baja segun el parametro.
	 * No incluye al responsable de cada unidad (no se da de baja a si mismo).
	 */
	@Query("select distinct p from Personal p join p.unidades u "
			+ "where u.id in :unidadIds and p.activo = :activo "
			+ "and (u.responsable is null or p.id <> u.responsable.id) "
			+ "order by p.apellido, p.nombre")
	List<Personal> findEmpleadosDeUnidades(@Param("unidadIds") Collection<Long> unidadIds,
			@Param("activo") boolean activo);

	/** Un empleado puntual, solo si pertenece a alguna de las unidades indicadas (y no es su responsable). */
	@Query("select distinct p from Personal p join p.unidades u "
			+ "where p.id = :id and u.id in :unidadIds "
			+ "and (u.responsable is null or p.id <> u.responsable.id)")
	Optional<Personal> findEmpleadoDeUnidades(@Param("id") Long id,
			@Param("unidadIds") Collection<Long> unidadIds);
}
