package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.PagoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoProveedorRepository extends JpaRepository<PagoProveedor, Long> {
    List<PagoProveedor> findByCuentaPagarId(Long cuentaPagarId);

    List<PagoProveedor> findByFechaPagoGreaterThanEqualAndFechaPagoLessThan(LocalDateTime desde, LocalDateTime hasta);
}