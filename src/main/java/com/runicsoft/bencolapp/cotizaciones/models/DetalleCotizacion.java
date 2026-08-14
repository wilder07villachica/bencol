package com.runicsoft.bencolapp.cotizaciones.models;

import com.runicsoft.bencolapp.productos.models.Producto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "detalles_cotizaciones")
@Data
public class DetalleCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "frecuencia_abastecimiento", length = 255)
    private String frecuenciaAbastecimiento;

    @Column(name = "descripcion_adicional", length = 500)
    private String descripcionAdicional;

    @Column(name = "imagen_nombre", length = 255)
    private String imagenNombre;

    @Column(name = "imagen_tipo", length = 100)
    private String imagenTipo;

    @Column(name = "imagen_ruta", length = 500)
    private String imagenRuta;
}