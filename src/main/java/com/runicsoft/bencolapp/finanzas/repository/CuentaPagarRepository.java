package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Query("""
        SELECT c
        FROM CuentaPagar c
        WHERE (:proveedorId IS NULL OR c.compra.proveedor.id = :proveedorId)
          AND (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.compra.fechaCreacion >= :desde)
          AND (:hasta IS NULL OR c.compra.fechaCreacion < :hasta)
        """)
    Page<CuentaPagar> buscar(
            @Param("proveedorId") Long proveedorId,
            @Param("estado") EstadoCuentaPagar estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}