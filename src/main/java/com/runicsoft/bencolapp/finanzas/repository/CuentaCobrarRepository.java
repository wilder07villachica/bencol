package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.CuentaCobrar;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaCobrarRepository extends JpaRepository<CuentaCobrar, Long> {
    boolean existsByVentaId(Long ventaId);
    Optional<CuentaCobrar> findByVentaId(Long ventaId);
    List<CuentaCobrar> findByEstado(EstadoCuenta estado);
    List<CuentaCobrar> findByVentaClienteId(Long clienteId);
}