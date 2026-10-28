package com.unla.epicentrogourmet.repositories;

import com.unla.epicentrogourmet.dtos.PlatoEstrellaDTO;
import com.unla.epicentrogourmet.entities.DetallesPedido;
import com.unla.epicentrogourmet.entities.Pedido;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDetallePedidoRepository extends JpaRepository<DetallesPedido, Long> {

    @Query(value = "SELECT pl.nombre, pl.precio_venta, SUM(dp.cantidad) AS unidades, COUNT(DISTINCT ped.id) AS pedidos " +
            "FROM detalles_pedido dp " +
            "INNER JOIN pedido ped ON dp.pedido_id = ped.id " +
            "INNER JOIN plato pl ON dp.plato_id = pl.id " +
            "WHERE ped.unidad_venta_id = :unidadId " +
            "  AND ped.festival_id = :festivalId " +
            "GROUP BY pl.id, pl.nombre, pl.precio_venta " +
            "ORDER BY unidades DESC, pedidos DESC " +
            "LIMIT 1", nativeQuery = true)
    List<Object[]> findPlatoEstrellaByUnidadYFestival(@Param("unidadId") Long unidadId,
                                           @Param("festivalId") Long festivalId);
}
