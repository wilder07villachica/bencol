package com.runicsoft.bencolapp.envases.repository;

import com.runicsoft.bencolapp.envases.models.CuentaEnvasesCliente;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CuentaEnvasesClienteRepository extends JpaRepository<CuentaEnvasesCliente, Long> {

    Optional<CuentaEnvasesCliente> findByClienteIdAndProductoId(Long clienteId, Long productoId);

    List<CuentaEnvasesCliente> findByClienteId(Long clienteId);

    List<CuentaEnvasesCliente> findByProductoId(Long productoId);

    boolean existsByClienteIdAndProductoId(Long clienteId, Long productoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c
            FROM CuentaEnvasesCliente c
            WHERE c.cliente.id = :clienteId
            AND c.producto.id = :productoId
            """)
    Optional<CuentaEnvasesCliente> findByClienteIdAndProductoIdForUpdate(
            @Param("clienteId") Long clienteId,
            @Param("productoId") Long productoId
    );
}