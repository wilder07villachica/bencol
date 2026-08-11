package com.runicsoft.bencolapp.compras.repository;

import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
        SELECT c
        FROM Compra c
        WHERE (:codigo IS NULL OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')))
          AND (:proveedorId IS NULL OR c.proveedor.id = :proveedorId)
          AND (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.fechaCreacion >= :desde)
          AND (:hasta IS NULL OR c.fechaCreacion < :hasta)
        """)
    Page<Compra> buscar(
            @Param("codigo") String codigo,
            @Param("proveedorId") Long proveedorId,
            @Param("estado") EstadoCompra estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}