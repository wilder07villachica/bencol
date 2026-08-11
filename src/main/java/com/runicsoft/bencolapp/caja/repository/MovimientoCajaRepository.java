package com.runicsoft.bencolapp.caja.repository;

import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByCajaId(Long cajaId);

    List<MovimientoCaja> findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(LocalDateTime desde, LocalDateTime hasta);

    // ===========================================================================================================
    @Query("""
            SELECT COALESCE(SUM(m.monto), 0)
            FROM MovimientoCaja m
            WHERE m.fechaMovimiento >= :desde
              AND m.fechaMovimiento < :hasta
              AND m.tipoMovimiento = :tipo
            """)
    BigDecimal sumMontoPeriodoByTipo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("tipo") TipoMovimientoCaja tipo);

    @Query("""
            SELECT YEAR(m.fechaMovimiento),
                   MONTH(m.fechaMovimiento),
                   COALESCE(SUM(m.monto), 0)
            FROM MovimientoCaja m
            WHERE m.fechaMovimiento >= :desde
              AND m.fechaMovimiento < :hasta
              AND m.tipoMovimiento = :tipo
            GROUP BY YEAR(m.fechaMovimiento), MONTH(m.fechaMovimiento)
            ORDER BY YEAR(m.fechaMovimiento), MONTH(m.fechaMovimiento)
            """)
    List<Object[]> findMovimientosAgrupadosPorMes(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, @Param("tipo") TipoMovimientoCaja tipo);

    @Query("""
        SELECT m
        FROM MovimientoCaja m
        WHERE (:cajaId IS NULL OR m.caja.id = :cajaId)
          AND (:tipoMovimiento IS NULL OR m.tipoMovimiento = :tipoMovimiento)
          AND (:desde IS NULL OR m.fechaMovimiento >= :desde)
          AND (:hasta IS NULL OR m.fechaMovimiento < :hasta)
        """)
    Page<MovimientoCaja> buscar(
            @Param("cajaId") Long cajaId,
            @Param("tipoMovimiento") TipoMovimientoCaja tipoMovimiento,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}