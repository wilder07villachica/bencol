package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.CuentaPagar;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaPagarRepository extends JpaRepository<CuentaPagar, Long> {
    boolean existsByCompraId(Long compraId);
    Optional<CuentaPagar> findByCompraId(Long compraId);
    List<CuentaPagar> findByEstado(EstadoCuentaPagar estado);
    List<CuentaPagar> findByCompraProveedorId(Long proveedorId);
}