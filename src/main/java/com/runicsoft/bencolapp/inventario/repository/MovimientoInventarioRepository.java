package com.runicsoft.bencolapp.inventario.repository;

import com.runicsoft.bencolapp.inventario.models.MovimientoInventario;
import com.runicsoft.bencolapp.inventario.utils.TipoMovimientoInventario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProductoId(Long productoId);

    @Query("""
        SELECT m
        FROM MovimientoInventario m
        WHERE (:productoId IS NULL OR m.producto.id = :productoId)
          AND (:tipoMovimiento IS NULL OR m.tipoMovimiento = :tipoMovimiento)
          AND (:desde IS NULL OR m.fechaCreacion >= :desde)
          AND (:hasta IS NULL OR m.fechaCreacion < :hasta)
        """)
    Page<MovimientoInventario> buscar(
            @Param("productoId") Long productoId,
            @Param("tipoMovimiento") TipoMovimientoInventario tipoMovimiento,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}