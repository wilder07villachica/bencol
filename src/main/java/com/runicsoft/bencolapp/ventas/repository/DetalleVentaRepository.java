package com.runicsoft.bencolapp.ventas.repository;

import com.runicsoft.bencolapp.ventas.models.DetalleVenta;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    @Query("""
            SELECT d.producto.id,
                   d.producto.codigo,
                   d.producto.descripcion,
                   SUM(d.cantidad),
                   SUM(d.cantidad * d.producto.unidadesPorPaquete),
                   SUM(d.subtotal)
            FROM DetalleVenta d
            WHERE d.venta.fechaCreacion >= :desde
              AND d.venta.fechaCreacion < :hasta
              AND d.venta.estado <> :estadoAnulada
            GROUP BY d.producto.id,
                     d.producto.codigo,
                     d.producto.descripcion
            ORDER BY SUM(d.cantidad) DESC
            """)
    List<Object[]> findProductosMasVendidos(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("estadoAnulada") EstadoVenta estadoAnulada
    );
}