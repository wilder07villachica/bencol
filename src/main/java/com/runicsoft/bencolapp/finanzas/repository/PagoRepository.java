package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByCuentaCobrarId(Long cuentaCobrarId);

    List<Pago> findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(LocalDateTime desde, LocalDateTime hasta);

    // ===========================================================================================================
    @Query("""
                SELECT COALESCE(SUM(p.monto), 0)
                FROM Pago p
                WHERE p.fechaPago >= :desde
                  AND p.fechaPago < :hasta
            """)
    BigDecimal sumPagosPeriodo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("""
            SELECT YEAR(p.fechaPago),
                   MONTH(p.fechaPago),
                   COALESCE(SUM(p.monto), 0)
            FROM Pago p
            WHERE p.fechaPago >= :desde
              AND p.fechaPago < :hasta
            GROUP BY YEAR(p.fechaPago), MONTH(p.fechaPago)
            ORDER BY YEAR(p.fechaPago), MONTH(p.fechaPago)
            """)
    List<Object[]> findIngresosAgrupadosPorMes(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}