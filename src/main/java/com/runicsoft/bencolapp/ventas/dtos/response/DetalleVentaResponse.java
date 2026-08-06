package com.runicsoft.bencolapp.ventas.dtos.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetalleVentaResponse {
    private Long id;
    private Long productoId;
    private String codigoProducto;
    private String descripcionProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}