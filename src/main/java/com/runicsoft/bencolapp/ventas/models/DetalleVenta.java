package com.runicsoft.bencolapp.ventas.models;

import com.runicsoft.bencolapp.productos.models.Producto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_ventas")
@Data
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(
            name = "precio_unitario",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
