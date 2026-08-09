package com.runicsoft.bencolapp.compras.repository;

import com.runicsoft.bencolapp.compras.models.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {
    List<DetalleCompra> findByProductoId(Long productoId);
}