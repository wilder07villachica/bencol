package com.runicsoft.bencolapp.inventario.repository;

import com.runicsoft.bencolapp.inventario.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByProductoId(Long productoId);
    Optional<Inventario> findByProductoId(Long productoId);
}