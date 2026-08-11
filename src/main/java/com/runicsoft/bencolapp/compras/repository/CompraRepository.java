package com.runicsoft.bencolapp.compras.repository;

import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    boolean existsByCodigo(String codigo);

    Optional<Compra> findByCodigo(String codigo);

    List<Compra> findByProveedorId(Long proveedorId);

    List<Compra> findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(LocalDateTime desde, LocalDateTime hasta);

    // ===========================================================================================================
    @Query("""
                SELECT COALESCE(SUM(c.total), 0)
                FROM Compra c
                WHERE c.fechaCreacion >= :desde
                  AND c.fechaCreacion < :hasta
                  AND c.estado <> :estadoAnulada
            """)
    BigDecimal sumTotalCompras(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("estadoAnulada") EstadoCompra estadoAnulada);

    @Query("""
                SELECT COUNT(c)
                FROM Compra c
                WHERE c.fechaCreacion >= :desde
                  AND c.fechaCreacion < :hasta
                  AND c.estado <> :estadoAnulada
            """)
    Long countCompras(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("estadoAnulada") EstadoCompra estadoAnulada);
}