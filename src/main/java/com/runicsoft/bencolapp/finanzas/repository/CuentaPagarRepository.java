package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CuentaPagarRepository extends JpaRepository<CuentaPagar, Long> {
    boolean existsByCompraId(Long compraId);

    Optional<CuentaPagar> findByCompraId(Long compraId);

    List<CuentaPagar> findByEstado(EstadoCuentaPagar estado);

    List<CuentaPagar> findByCompraProveedorId(Long proveedorId);

    // ===========================================================================================================
    @Query("""
                SELECT COALESCE(SUM(c.saldoPendiente), 0)
                FROM CuentaPagar c
                WHERE c.estado IN :estados
            """)
    BigDecimal sumSaldoPendienteByEstados(@Param("estados") List<EstadoCuentaPagar> estados);

    @Query("""
                SELECT COALESCE(SUM(c.montoPagado), 0)
                FROM CuentaPagar c
                WHERE c.estado <> :estadoAnulada
            """)
    BigDecimal sumMontoPagadoNoAnuladas(@Param("estadoAnulada") EstadoCuentaPagar estadoAnulada);
}