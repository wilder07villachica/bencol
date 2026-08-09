package com.runicsoft.bencolapp.compras.repository;

import com.runicsoft.bencolapp.compras.models.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    boolean existsByCodigo(String codigo);
    Optional<Compra> findByCodigo(String codigo);
    List<Compra> findByProveedorId(Long proveedorId);

    List<Compra> findByFechaCreacionGreaterThanEqualAndFechaCreacionLessThan(LocalDateTime desde, LocalDateTime hasta);
}