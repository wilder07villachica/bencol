package com.runicsoft.bencolapp.ventas.repository;

import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    boolean existsByCodigo(String codigo);

    Optional<Venta> findByCodigo(String codigo);

    List<Venta> findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(LocalDateTime desde, LocalDateTime hasta);

    // ===========================================================================================================
    @Query("""
            SELECT COALESCE(SUM(v.total), 0)
            FROM Venta v
            WHERE v.fechaCreacion >= :desde
              AND v.fechaCreacion < :hasta
              AND v.estado <> :estadoAnulada
            """)
    BigDecimal sumTotalVentas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("estadoAnulada") EstadoVenta estadoAnulada);

    @Query("""
            SELECT COUNT(v)
            FROM Venta v
            WHERE v.fechaCreacion >= :desde
              AND v.fechaCreacion < :hasta
              AND v.estado <> :estadoAnulada
            """)
    Long countVentas(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("estadoAnulada") EstadoVenta estadoAnulada);

    @Query("""
            SELECT YEAR(v.fechaCreacion),
                   MONTH(v.fechaCreacion),
                   COUNT(v),
                   COALESCE(SUM(v.total), 0)
            FROM Venta v
            WHERE v.fechaCreacion >= :desde
              AND v.fechaCreacion < :hasta
              AND v.estado <> :estadoAnulada
            GROUP BY YEAR(v.fechaCreacion), MONTH(v.fechaCreacion)
            ORDER BY YEAR(v.fechaCreacion), MONTH(v.fechaCreacion)
            """)
    List<Object[]> findVentasAgrupadasPorMes(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("estadoAnulada") EstadoVenta estadoAnulada);

    @Query("""
        SELECT v
        FROM Venta v
        WHERE (:codigo IS NULL OR LOWER(v.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')))
          AND (:clienteId IS NULL OR v.cliente.id = :clienteId)
          AND (:estado IS NULL OR v.estado = :estado)
          AND (:desde IS NULL OR v.fechaCreacion >= :desde)
          AND (:hasta IS NULL OR v.fechaCreacion < :hasta)
        """)
    Page<Venta> buscar(
            @Param("codigo") String codigo,
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoVenta estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}