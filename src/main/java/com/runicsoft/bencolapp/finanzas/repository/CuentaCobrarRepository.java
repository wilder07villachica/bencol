package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CuentaCobrarRepository extends JpaRepository<CuentaCobrar, Long> {
    boolean existsByVentaId(Long ventaId);

    Optional<CuentaCobrar> findByVentaId(Long ventaId);

    List<CuentaCobrar> findByEstado(EstadoCuenta estado);

    List<CuentaCobrar> findByVentaClienteId(Long clienteId);

    // ===========================================================================================================
    @Query("""
                SELECT COALESCE(SUM(c.saldoPendiente), 0)
                FROM CuentaCobrar c
                WHERE c.estado IN :estados
            """)
    BigDecimal sumSaldoPendienteByEstados(@Param("estados") List<EstadoCuenta> estados);

    @Query("""
                SELECT COALESCE(SUM(c.montoPagado), 0)
                FROM CuentaCobrar c
                WHERE c.estado <> :estadoAnulada
            """)
    BigDecimal sumMontoPagadoNoAnuladas(@Param("estadoAnulada") EstadoCuenta estadoAnulada);

    @Query("""
        SELECT c
        FROM CuentaCobrar c
        WHERE (:clienteId IS NULL OR c.venta.cliente.id = :clienteId)
          AND (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.venta.fechaCreacion >= :desde)
          AND (:hasta IS NULL OR c.venta.fechaCreacion < :hasta)
        """)
    Page<CuentaCobrar> buscar(
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoCuenta estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}