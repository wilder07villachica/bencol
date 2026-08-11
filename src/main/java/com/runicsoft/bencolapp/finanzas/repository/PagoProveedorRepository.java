package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.PagoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoProveedorRepository extends JpaRepository<PagoProveedor, Long> {
    List<PagoProveedor> findByCuentaPagarId(Long cuentaPagarId);

    List<PagoProveedor> findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(LocalDateTime desde, LocalDateTime hasta);

    // ===========================================================================================================
    @Query("""
                SELECT COALESCE(SUM(p.monto), 0)
                FROM PagoProveedor p
                WHERE p.fechaPago >= :desde
                  AND p.fechaPago < :hasta
            """)
    BigDecimal sumPagosPeriodo(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}