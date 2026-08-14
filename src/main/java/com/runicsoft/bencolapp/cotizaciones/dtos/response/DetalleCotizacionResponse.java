package com.runicsoft.bencolapp.cotizaciones.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleCotizacionResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private String categoriaProducto;
    private Double contenidoProducto;
    private String unidadMedidaProducto;
    private Integer unidadesPorPaquete;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String frecuenciaAbastecimiento;
    private String descripcionAdicional;
    private String imagenNombre;
    private String imagenTipo;
    private Boolean tieneImagen;
}