package com.runicsoft.bencolapp.ventas.repository;

import com.runicsoft.bencolapp.ventas.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    boolean existsByCodigo(String codigo);
    Optional<Venta> findByCodigo(String codigo);
}