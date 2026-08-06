package com.runicsoft.bencolapp.ventas.repository;

import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
}